import { Button, Classes, Dialog } from '@blueprintjs/core';
import { useMutation } from '@tanstack/react-query';

import { updateProblemSetMutationOptions } from '../../../../modules/queries/problemSet';
import ProblemSetEditForm from '../ProblemSetEditForm/ProblemSetEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function ProblemSetEditDialog({ isOpen, problemSet, archiveSlug, onCloseDialog }) {
  const updateProblemSetMutation = useMutation(updateProblemSetMutationOptions(problemSet?.jid));

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

  const updateProblemSet = async data => {
    await updateProblemSetMutation.mutateAsync(
      {
        slug: data.slug,
        name: data.name,
        archiveSlug: data.archiveSlug,
        description: data.description,
        contestTime: new Date(data.contestTime).getTime(),
      },
      {
        onSuccess: () => {
          toastActions.showSuccessToast('Problemset updated.');
        },
      }
    );
    onCloseDialog();
  };

  const initialValues = problemSet && {
    slug: problemSet.slug,
    name: problemSet.name,
    archiveSlug: archiveSlug,
    description: problemSet.description,
    contestTime: new Date(problemSet.contestTime).toISOString(),
  };

  return (
    <div className="content-card__section">
      <Dialog isOpen={isOpen} onClose={onCloseDialog} title="Edit problemset" canOutsideClickClose={false}>
        <ProblemSetEditForm
          renderFormComponents={renderDialogForm}
          onSubmit={updateProblemSet}
          initialValues={initialValues}
        />
      </Dialog>
    </div>
  );
}
