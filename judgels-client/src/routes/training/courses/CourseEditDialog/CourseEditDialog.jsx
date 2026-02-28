import { Button, Classes, Dialog } from '@blueprintjs/core';
import { useMutation } from '@tanstack/react-query';

import { updateCourseMutationOptions } from '../../../../modules/queries/course';
import CourseEditForm from '../CourseEditForm/CourseEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function CourseEditDialog({ course, isOpen, onCloseDialog }) {
  const updateCourseMutation = useMutation(updateCourseMutationOptions(course?.jid));

  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={Classes.DIALOG_BODY}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={onCloseDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const updateCourse = async data => {
    await updateCourseMutation.mutateAsync(data, {
      onSuccess: () => {
        toastActions.showSuccessToast('Course updated.');
      },
    });
    onCloseDialog();
  };

  const initialValues = course && {
    slug: course.slug,
    name: course.name,
    description: course.description,
  };

  return (
    <div className="content-card__section">
      <Dialog isOpen={isOpen} onClose={onCloseDialog} title="Edit course" canOutsideClickClose={false}>
        <CourseEditForm renderFormComponents={renderDialogForm} onSubmit={updateCourse} initialValues={initialValues} />
      </Dialog>
    </div>
  );
}
