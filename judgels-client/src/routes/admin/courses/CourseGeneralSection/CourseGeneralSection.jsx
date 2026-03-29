import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { updateCourseMutationOptions } from '../../../../modules/queries/course';
import CourseGeneralEditForm from '../CourseGeneralEditForm/CourseGeneralEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function CourseGeneralSection({ course }) {
  const updateCourseMutation = useMutation(updateCourseMutationOptions(course.jid));

  const [isEditing, setIsEditing] = useState(false);

  const keyStyles = { width: '250px' };

  const rows = [
    { key: 'slug', title: 'Slug', value: course.slug },
    { key: 'name', title: 'Name', value: course.name },
    { key: 'description', title: 'Description', value: course.description },
  ];

  const updateCourse = async data => {
    await updateCourseMutation.mutateAsync(data, {
      onSuccess: () => toastActions.showSuccessToast('Course updated.'),
    });
    setIsEditing(false);
  };

  const renderEditButton = () => {
    return (
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
        slug: course.slug || '',
        name: course.name || '',
        description: course.description || '',
      };
      return (
        <CourseGeneralEditForm
          initialValues={initialValues}
          onSubmit={updateCourse}
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
