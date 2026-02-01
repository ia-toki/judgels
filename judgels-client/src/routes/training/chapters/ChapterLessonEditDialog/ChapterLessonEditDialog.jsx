import { Button, Callout, Classes, Dialog, Intent } from '@blueprintjs/core';
import { useEffect, useState } from 'react';

import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { Alias } from '../../../../components/forms/validations';
import ChapterLessonEditForm from '../ChapterLessonEditForm/ChapterLessonEditForm';
import { ChapterLessonsTable } from '../ChapterLessonsTable/ChapterLessonsTable';

export function ChapterLessonEditDialog({ isOpen, chapter, onGetLessons, onSetLessons, onCloseDialog }) {
  const [state, setState] = useState({
    response: undefined,
    isEditing: false,
  });

  const refreshLessons = async () => {
    if (isOpen) {
      setState(prevState => ({ ...prevState, response: undefined }));
      const response = await onGetLessons(chapter.jid);
      setState(prevState => ({ ...prevState, response }));
    }
  };

  useEffect(() => {
    refreshLessons();
  }, [chapter]);

  const render = () => {
    return (
      <div className="content-card__section">
        <Dialog isOpen={isOpen} onClose={closeDialog} title="Edit chapter lessons" canOutsideClickClose={false}>
          {renderDialogContent()}
        </Dialog>
      </div>
    );
  };

  const closeDialog = () => {
    onCloseDialog();
    setState(prevState => ({ ...prevState, isEditing: false }));
  };

  const renderDialogContent = () => {
    const { response, isEditing } = state;
    if (!response) {
      return renderDialogForm(<LoadingState />, null);
    }

    if (isEditing) {
      const props = {
        validator: validateLessons,
        renderFormComponents: renderDialogForm,
        onSubmit: updateLessons,
        initialValues: { lessons: serializeLessons(response.data, response.lessonsMap) },
      };
      return <ChapterLessonEditForm {...props} />;
    } else {
      const content = <ChapterLessonsTable response={response} />;
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
    if (!state.isEditing) {
      return null;
    }

    return (
      <Callout icon={null}>
        <p>
          <strong>Format:</strong> <code>alias,slug</code>
        </p>
        <p>
          <strong>Example:</strong>
        </p>
        <pre>{'A,hello\nB,tree'}</pre>
      </Callout>
    );
  };

  const toggleEditing = () => {
    setState(prevState => ({
      ...prevState,
      isEditing: !prevState.isEditing,
    }));
  };

  const updateLessons = async data => {
    const lessons = deserializeLessons(data.lessons);
    await onSetLessons(chapter.jid, lessons);
    await refreshLessons();
    toggleEditing();
  };

  const serializeLessons = (lessons, lessonsMap) => {
    return lessons.map(c => `${c.alias},${lessonsMap[c.lessonJid].slug}`).join('\n');
  };

  const deserializeLessons = lessons => {
    return lessons
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()))
      .map(s => ({
        alias: s[0],
        slug: s[1],
      }));
  };

  const validateLessons = value => {
    const lessons = value
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()));

    const aliases = [];
    const slugs = [];

    for (const c of lessons) {
      if (c.length !== 2) {
        return 'Each line must contain 2 comma-separated elements';
      }
      const alias = c[0];
      const aliasValidation = Alias(alias);
      if (aliasValidation) {
        return 'Lesson aliases: ' + aliasValidation;
      }

      const slug = c[1];

      aliases.push(alias);
      slugs.push(slug);
    }

    if (new Set(aliases).size !== aliases.length) {
      return 'Lesson aliases must be unique';
    }
    if (new Set(slugs).size !== slugs.length) {
      return 'Lesson slugs must be unique';
    }

    return undefined;
  };

  return render();
}
