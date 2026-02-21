import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Trash } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import classNames from 'classnames';
import { useState } from 'react';

import { deleteContestSupervisorsMutationOptions } from '../../../../../../modules/queries/contestSupervisor';
import ContestSupervisorRemoveForm from '../ContestSupervisorRemoveForm/ContestSupervisorRemoveForm';
import { ContestSupervisorRemoveResultTable } from '../ContestSupervisorRemoveResultTable/ContestSupervisorRemoveResultTable';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export function ContestSupervisorRemoveDialog({ contest }) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [submitted, setSubmitted] = useState(undefined);

  const deleteSupervisorsMutation = useMutation(deleteContestSupervisorsMutationOptions(contest.jid));

  const toggleDialog = () => {
    setIsDialogOpen(open => !open);
    setSubmitted(undefined);
  };

  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={classNames(Classes.DIALOG_BODY, 'contest-supervisor-dialog-body')}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const removeSupervisors = async data => {
    const usernames = data.usernames
      .split('\n')
      .filter(s => s.length > 0)
      .map(s => s.trim());
    const response = await deleteSupervisorsMutation.mutateAsync(usernames);
    if (usernames.length === Object.keys(response.deletedSupervisorProfilesMap).length) {
      toastActions.showSuccessToast('Supervisors removed.');
      setIsDialogOpen(false);
    } else {
      setSubmitted({ usernames, response });
    }
  };

  const renderDialogRemoveResultTable = () => {
    const { usernames, response } = submitted;
    const { deletedSupervisorProfilesMap } = response;
    return (
      <>
        <div className={classNames(Classes.DIALOG_BODY, 'contest-supervisor-dialog-result-body')}>
          <ContestSupervisorRemoveResultTable
            usernames={usernames}
            deletedSupervisorProfilesMap={deletedSupervisorProfilesMap}
          />
        </div>
        <div className={Classes.DIALOG_FOOTER}>
          <div className={Classes.DIALOG_FOOTER_ACTIONS}>
            <Button text="Done" intent={Intent.PRIMARY} onClick={toggleDialog} />
          </div>
        </div>
      </>
    );
  };

  const dialogBody =
    submitted !== undefined ? (
      renderDialogRemoveResultTable()
    ) : (
      <ContestSupervisorRemoveForm renderFormComponents={renderDialogForm} onSubmit={removeSupervisors} />
    );
  const dialogTitle = submitted !== undefined ? 'Remove supervisors results' : 'Remove supervisors';

  return (
    <div className="content-card__section">
      <Button
        className="contest-supervisor-dialog-button"
        intent={Intent.DANGER}
        icon={<Trash />}
        onClick={toggleDialog}
        disabled={isDialogOpen}
      >
        Remove supervisors
      </Button>
      <Dialog
        className="contest-supervisor-dialog"
        isOpen={isDialogOpen}
        onClose={toggleDialog}
        title={dialogTitle}
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        {dialogBody}
      </Dialog>
    </div>
  );
}
