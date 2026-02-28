import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { createProblemSetMutationOptions } from '../../../../modules/queries/problemSet';
import ProblemSetCreateForm from '../ProblemSetCreateForm/ProblemSetCreateForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function ProblemSetCreateDialog() {
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const createProblemSetMutation = useMutation(createProblemSetMutationOptions);

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

  const createProblemSet = async data => {
    await createProblemSetMutation.mutateAsync(
      {
        slug: data.slug,
        name: data.name,
        archiveSlug: data.archiveSlug,
        description: data.description,
        contestTime: new Date(data.contestTime).getTime(),
      },
      {
        onSuccess: () => {
          toastActions.showSuccessToast('Problemset created.');
        },
      }
    );
    setIsDialogOpen(false);
  };

  const initialValues = {
    contestTime: new Date().toISOString(),
  };

  return (
    <div className="content-card__section">
      <Button intent={Intent.PRIMARY} icon={<Plus />} onClick={toggleDialog} disabled={isDialogOpen}>
        New problemset
      </Button>
      <Dialog isOpen={isDialogOpen} onClose={toggleDialog} title="Create new problemset" canOutsideClickClose={false}>
        <ProblemSetCreateForm
          renderFormComponents={renderDialogForm}
          onSubmit={createProblemSet}
          initialValues={initialValues}
        />
      </Dialog>
    </div>
  );
}
