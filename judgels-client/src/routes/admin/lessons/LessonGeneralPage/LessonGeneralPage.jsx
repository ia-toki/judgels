import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { UserRef } from '../../../../components/UserRef/UserRef';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import {
  lessonBySlugQueryOptions,
  lessonQueryOptions,
  updateLessonMutationOptions,
} from '../../../../modules/queries/lesson';
import LessonGeneralEditForm from '../LessonGeneralEditForm/LessonGeneralEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function LessonGeneralPage() {
  const navigate = useNavigate();
  const { lessonSlug } = useParams({ strict: false });

  const {
    data: { jid: lessonJid },
  } = useSuspenseQuery(lessonBySlugQueryOptions(lessonSlug));

  const {
    data: { data: lesson, config, profilesMap },
  } = useSuspenseQuery(lessonQueryOptions(lessonJid));

  const updateLessonMutation = useMutation(updateLessonMutationOptions(lessonJid, lessonSlug));

  const [isEditing, setIsEditing] = useState(false);

  const keyStyles = { width: '250px' };

  const rows = [
    { key: 'jid', title: 'JID', value: lesson.jid },
    { key: 'slug', title: 'Slug', value: lesson.slug },
    { key: 'author', title: 'Author', value: <UserRef profile={profilesMap[lesson.authorJid]} /> },
    { key: 'additionalNote', title: 'Additional note', value: lesson.additionalNote },
  ];

  const updateLesson = async data => {
    await updateLessonMutation.mutateAsync(
      { slug: data.slug, additionalNote: data.additionalNote || '' },
      {
        onSuccess: () => toastActions.showSuccessToast('Lesson updated.'),
      }
    );
    setIsEditing(false);
    if (data.slug !== lessonSlug) {
      navigate({ to: `/admin/lessons/${data.slug}` });
    }
  };

  const renderEditButton = () => {
    return (
      config.canEdit &&
      !isEditing && (
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = {
        slug: lesson.slug,
        additionalNote: lesson.additionalNote,
      };
      return (
        <LessonGeneralEditForm
          initialValues={initialValues}
          onSubmit={updateLesson}
          onCancel={() => setIsEditing(false)}
        />
      );
    }
    return <FormTable keyStyles={keyStyles} rows={rows} />;
  };

  return (
    <div>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>General</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
