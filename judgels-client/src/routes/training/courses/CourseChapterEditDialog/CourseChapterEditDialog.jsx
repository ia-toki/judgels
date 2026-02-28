import { Button, Callout, Classes, Dialog, Intent } from '@blueprintjs/core';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { Alias } from '../../../../components/forms/validations';
import { courseChaptersQueryOptions, setCourseChaptersMutationOptions } from '../../../../modules/queries/course';
import CourseChapterEditForm from '../CourseChapterEditForm/CourseChapterEditForm';
import { CourseChaptersTable } from '../CourseChaptersTable/CourseChaptersTable';

import * as toastActions from '../../../../modules/toast/toastActions';

export function CourseChapterEditDialog({ isOpen, course, onCloseDialog }) {
  const [isEditing, setIsEditing] = useState(false);

  const { data: response } = useQuery({
    ...courseChaptersQueryOptions(course?.jid),
    enabled: isOpen && !!course,
  });

  const setChaptersMutation = useMutation(setCourseChaptersMutationOptions(course?.jid));

  const closeDialog = () => {
    onCloseDialog();
    setIsEditing(false);
  };

  const toggleEditing = () => {
    setIsEditing(prev => !prev);
  };

  const updateChapters = async data => {
    const chapters = deserializeChapters(data.chapters);
    await setChaptersMutation.mutateAsync(chapters, {
      onSuccess: () => {
        toastActions.showSuccessToast('Course chapters updated.');
      },
    });
    setIsEditing(false);
  };

  const renderDialogContent = () => {
    if (!response) {
      return renderDialogForm(<LoadingState />, null);
    }

    if (isEditing) {
      const props = {
        validator: validateChapters,
        renderFormComponents: renderDialogForm,
        onSubmit: updateChapters,
        initialValues: { chapters: serializeChapters(response.data) },
      };
      return <CourseChapterEditForm {...props} />;
    } else {
      const content = <CourseChaptersTable response={response} />;
      const submitButton = <Button data-key="edit" text="Edit" intent={Intent.PRIMARY} onClick={toggleEditing} />;
      return renderDialogForm(content, submitButton);
    }
  };

  const renderDialogForm = (content, submitButton) => (
    <>
      <div className={Classes.DIALOG_BODY}>
        {content}
        {renderInstructions()}
      </div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={closeDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const renderInstructions = () => {
    if (!isEditing) {
      return null;
    }

    return (
      <Callout icon={null}>
        <p>
          <strong>Format:</strong> <code>alias,chapterJid</code>
        </p>
        <p>
          <strong>Example:</strong>
        </p>
        <pre>{'A,JIDSESS123\nB,JIDSESS456'}</pre>
      </Callout>
    );
  };

  return (
    <div className="content-card__section">
      <Dialog isOpen={isOpen} onClose={closeDialog} title="Edit course chapters" canOutsideClickClose={false}>
        {renderDialogContent()}
      </Dialog>
    </div>
  );
}

function serializeChapters(chapters) {
  return chapters.map(c => `${c.alias},${c.chapterJid}`).join('\n');
}

function deserializeChapters(chapters) {
  return chapters
    .split('\n')
    .map(s => s.trim())
    .filter(s => s.length > 0)
    .map(s => s.split(','))
    .map(s => s.map(t => t.trim()))
    .map(s => ({
      alias: s[0],
      chapterJid: s[1],
    }));
}

function validateChapters(value) {
  const chapters = value
    .split('\n')
    .map(s => s.trim())
    .filter(s => s.length > 0)
    .map(s => s.split(','))
    .map(s => s.map(t => t.trim()));

  const aliases = [];
  const chapterJids = [];

  for (const c of chapters) {
    if (c.length !== 2) {
      return 'Each line must contain 2 comma-separated elements';
    }
    const alias = c[0];
    const aliasValidation = Alias(alias);
    if (aliasValidation) {
      return 'Chapter aliases: ' + aliasValidation;
    }

    const chapterJid = c[1];

    aliases.push(alias);
    chapterJids.push(chapterJid);
  }

  if (new Set(aliases).size !== aliases.length) {
    return 'Chapter aliases must be unique';
  }
  if (new Set(chapterJids).size !== chapterJids.length) {
    return 'Chapter JIDs must be unique';
  }

  return undefined;
}
