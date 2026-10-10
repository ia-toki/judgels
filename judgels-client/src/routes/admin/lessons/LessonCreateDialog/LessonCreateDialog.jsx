import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';
import { useState } from 'react';

import { createLessonMutationOptions } from '../../../../modules/queries/lesson';
import LessonCreateForm from '../LessonCreateForm/LessonCreateForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function LessonCreateDialog() {
  const navigate = useNavigate();
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const createLessonMutation = useMutation(createLessonMutationOptions);

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

  const createLesson = async data => {
    await createLessonMutation.mutateAsync(
      { ...data, additionalNote: data.additionalNote || '' },
      {
        onSuccess: lesson => {
          setIsDialogOpen(false);
          navigate({ to: `/admin/lessons/${lesson.jid}` });
          toastActions.showSuccessToast('Lesson created.');
        },
      }
    );
  };

  return (
    <>
      <Button intent={Intent.PRIMARY} icon={<Plus />} onClick={toggleDialog} disabled={isDialogOpen}>
        New lesson
      </Button>
      <Dialog isOpen={isDialogOpen} onClose={toggleDialog} title="Create new lesson" canOutsideClickClose={false}>
        <LessonCreateForm renderFormComponents={renderDialogForm} onSubmit={createLesson} />
      </Dialog>
    </>
  );
}
