import { Button, Callout, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { Alias } from '../../../../components/forms/validations';
import {
  courseBySlugQueryOptions,
  courseChaptersQueryOptions,
  setCourseChaptersMutationOptions,
  updateCourseMutationOptions,
} from '../../../../modules/queries/course';
import CourseChapterEditForm from '../CourseChapterEditForm/CourseChapterEditForm';
import { CourseChaptersTable } from '../CourseChaptersTable/CourseChaptersTable';
import CourseEditForm from '../CourseEditForm/CourseEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function CourseViewPage() {
  const { courseSlug } = useParams({ strict: false });

  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(courseSlug));
  const { data: chaptersResponse } = useSuspenseQuery(courseChaptersQueryOptions(course.jid));

  const updateCourseMutation = useMutation(updateCourseMutationOptions(course.jid));
  const setChaptersMutation = useMutation(setCourseChaptersMutationOptions(course.jid));

  const [isEditingGeneral, setIsEditingGeneral] = useState(false);
  const [isEditingChapters, setIsEditingChapters] = useState(false);

  const keyStyles = { width: '220px' };

  const detailRows = [
    { key: 'slug', title: 'Slug', value: course.slug },
    { key: 'name', title: 'Name', value: course.name },
    { key: 'description', title: 'Description', value: course.description },
  ];

  const updateCourse = async data => {
    await updateCourseMutation.mutateAsync(data, {
      onSuccess: () => toastActions.showSuccessToast('Course updated.'),
    });
    setIsEditingGeneral(false);
  };

  const updateChapters = data => {
    const chapters = deserializeChapters(data.chapters);
    setChaptersMutation.mutate(chapters, {
      onSuccess: () => toastActions.showSuccessToast('Course chapters updated.'),
    });
    setIsEditingChapters(false);
  };

  const renderGeneralEditButton = () => {
    return (
      !isEditingGeneral && (
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditingGeneral(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderGeneralSection = () => {
    const renderGeneralContent = () => {
      if (isEditingGeneral) {
        const initialValues = {
          slug: course.slug || '',
          name: course.name || '',
          description: course.description || '',
        };
        return (
          <CourseEditForm
            initialValues={initialValues}
            onSubmit={updateCourse}
            onCancel={() => setIsEditingGeneral(false)}
          />
        );
      }
      return <FormTable keyStyles={keyStyles} rows={detailRows} />;
    };

    return (
      <div>
        <Flex asChild justifyContent="space-between" alignItems="baseline">
          <h4>
            <span>General</span>
            {renderGeneralEditButton()}
          </h4>
        </Flex>
        {renderGeneralContent()}
      </div>
    );
  };

  const renderChaptersEditButton = () => {
    return (
      !isEditingChapters && (
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditingChapters(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderChaptersSection = () => {
    const renderChaptersContent = () => {
      if (isEditingChapters) {
        const initialValues = { chapters: serializeChapters(chaptersResponse.data) };
        const renderFormComponents = (fields, submitButton) => (
          <Flex flexDirection="column" gap={2}>
            {fields}
            {renderChapterInstructions()}
            <ActionButtons justifyContent="end">
              <Button text="Cancels" onClick={() => setIsEditingChapters(false)} />
              {submitButton}
            </ActionButtons>
          </Flex>
        );
        return (
          <CourseChapterEditForm
            initialValues={initialValues}
            validator={validateChapters}
            onSubmit={updateChapters}
            renderFormComponents={renderFormComponents}
          />
        );
      }
      return <CourseChaptersTable response={chaptersResponse} />;
    };

    return (
      <div>
        <Flex asChild justifyContent="space-between" alignItems="baseline">
          <h4>
            <span>Chapters</span>
            {renderChaptersEditButton()}
          </h4>
        </Flex>
        {renderChaptersContent()}
      </div>
    );
  };

  const renderChapterInstructions = () => {
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
    <ContentCard title={`Courses › ${course.slug}`}>
      {renderGeneralSection()}
      <hr />
      {renderChaptersSection()}
    </ContentCard>
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
