import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';
import { useState } from 'react';

import { createProblemMutationOptions } from '../../../../modules/queries/problem';
import ProblemCreateForm from '../ProblemCreateForm/ProblemCreateForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function ProblemCreateDialog() {
  const navigate = useNavigate();
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const createProblemMutation = useMutation(createProblemMutationOptions);

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

  const createProblem = async data => {
    await createProblemMutation.mutateAsync(
      { ...data, additionalNote: data.additionalNote || '' },
      {
        onSuccess: problem => {
          setIsDialogOpen(false);
          navigate({ to: `/admin/problems/${problem.jid}` });
          toastActions.showSuccessToast('Problem created.');
        },
      }
    );
  };

  return (
    <>
      <Button intent={Intent.PRIMARY} icon={<Plus />} onClick={toggleDialog} disabled={isDialogOpen}>
        New problem
      </Button>
      <Dialog isOpen={isDialogOpen} onClose={toggleDialog} title="Create new problem" canOutsideClickClose={false}>
        <ProblemCreateForm renderFormComponents={renderDialogForm} onSubmit={createProblem} />
      </Dialog>
    </>
  );
}
