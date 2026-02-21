import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { createContestClarificationMutationOptions } from '../../../../../../modules/queries/contestClarification';
import ContestClarificationCreateForm from '../ContestClarificationCreateForm/ContestClarificationCreateForm';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export function ContestClarificationCreateDialog({ contest, problemJids, problemAliasesMap, problemNamesMap }) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const createClarificationMutation = useMutation(createContestClarificationMutationOptions(contest.jid));

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

  const createClarification = async data => {
    await createClarificationMutation.mutateAsync(data, {
      onSuccess: () => {
        toastActions.showSuccessToast('Clarification submitted.');
      },
      onSettled: () => {
        setIsDialogOpen(false);
      },
    });
  };

  const props = {
    contestJid: contest.jid,
    problemJids,
    problemAliasesMap,
    problemNamesMap,
    renderFormComponents: renderDialogForm,
    onSubmit: createClarification,
    initialValues: {
      topicJid: contest.jid,
    },
  };

  return (
    <div className="content-card__section">
      <Button intent={Intent.PRIMARY} icon={<Plus />} onClick={toggleDialog} disabled={isDialogOpen}>
        New clarification
      </Button>
      <Dialog
        isOpen={isDialogOpen}
        onClose={toggleDialog}
        title="Submit new clarification"
        canOutsideClickClose={false}
      >
        <ContestClarificationCreateForm {...props} />
      </Dialog>
    </div>
  );
}
