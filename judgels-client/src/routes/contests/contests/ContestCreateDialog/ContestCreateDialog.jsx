import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from '@tanstack/react-router';
import { useState } from 'react';

import { createContestMutationOptions } from '../../../../modules/queries/contest';
import ContestCreateForm from '../ContestCreateForm/ContestCreateForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function ContestCreateDialog() {
  const navigate = useNavigate();
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const createContestMutation = useMutation(createContestMutationOptions());

  const render = () => {
    return (
      <div className="content-card__section">
        {renderButton()}
        {renderDialog()}
      </div>
    );
  };

  const renderButton = () => {
    return (
      <Button intent={Intent.PRIMARY} icon={<Plus />} onClick={toggleDialog} disabled={isDialogOpen}>
        New contest
      </Button>
    );
  };

  const toggleDialog = () => {
    setIsDialogOpen(open => !open);
  };

  const renderDialog = () => {
    const props = {
      renderFormComponents: renderDialogForm,
      onSubmit: createContest,
    };
    return (
      <Dialog isOpen={isDialogOpen} onClose={toggleDialog} title="Create new contest" canOutsideClickClose={false}>
        <ContestCreateForm {...props} />
      </Dialog>
    );
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

  const createContest = async data => {
    await createContestMutation.mutateAsync(data, {
      onSuccess: (_data, { slug }) => {
        navigate({ to: `/contests/${slug}`, state: { isEditingContest: true } });
        toastActions.showSuccessToast('Contest created.');
      },
      onSettled: () => {
        setIsDialogOpen(false);
      },
    });
  };

  return render();
}
