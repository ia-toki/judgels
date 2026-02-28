import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { createCourseMutationOptions } from '../../../../modules/queries/course';
import CourseCreateForm from '../CourseCreateForm/CourseCreateForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function CourseCreateDialog() {
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const createCourseMutation = useMutation(createCourseMutationOptions);

  const toggleDialog = () => {
    setIsDialogOpen(open => !open);
  };

  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={Classes.DIALOG_BODY}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const createCourse = async data => {
    await createCourseMutation.mutateAsync(data, {
      onSuccess: () => {
        toastActions.showSuccessToast('Course created.');
      },
    });
    setIsDialogOpen(false);
  };

  return (
    <div className="content-card__section">
      <Button intent={Intent.PRIMARY} icon={<Plus />} onClick={toggleDialog} disabled={isDialogOpen}>
        New course
      </Button>
      <Dialog isOpen={isDialogOpen} onClose={toggleDialog} title="Create new course" canOutsideClickClose={false}>
        <CourseCreateForm renderFormComponents={renderDialogForm} onSubmit={createCourse} />
      </Dialog>
    </div>
  );
}
