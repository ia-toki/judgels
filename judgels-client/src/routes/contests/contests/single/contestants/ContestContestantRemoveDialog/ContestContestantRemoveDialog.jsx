import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Trash } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import classNames from 'classnames';
import { useState } from 'react';

import { deleteContestContestantsMutationOptions } from '../../../../../../modules/queries/contestContestant';
import ContestContestantRemoveForm from '../ContestContestantRemoveForm/ContestContestantRemoveForm';
import { ContestContestantRemoveResultTable } from '../ContestContestantRemoveResultTable/ContestContestantRemoveResultTable';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export function ContestContestantRemoveDialog({ contest }) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [submitted, setSubmitted] = useState(undefined);

  const deleteContestantsMutation = useMutation(deleteContestContestantsMutationOptions(contest.jid));

  const toggleDialog = () => {
    setIsDialogOpen(open => !open);
    setSubmitted(undefined);
  };

  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={classNames(Classes.DIALOG_BODY, 'contest-contestant-dialog-body')}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const removeContestants = async data => {
    const usernames = data.usernames
      .split('\n')
      .filter(s => s.length > 0)
      .map(s => s.trim());
    const response = await deleteContestantsMutation.mutateAsync(usernames);
    if (usernames.length === Object.keys(response.deletedContestantProfilesMap).length) {
      toastActions.showSuccessToast('Contestants removed.');
      setIsDialogOpen(false);
    } else {
      setSubmitted({ usernames, response });
    }
  };

  const renderDialogRemoveResultTable = () => {
    const { usernames, response } = submitted;
    const { deletedContestantProfilesMap } = response;
    return (
      <>
        <div className={classNames(Classes.DIALOG_BODY, 'contest-contestant-dialog-result-body')}>
          <ContestContestantRemoveResultTable
            usernames={usernames}
            deletedContestantProfilesMap={deletedContestantProfilesMap}
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
      <ContestContestantRemoveForm renderFormComponents={renderDialogForm} onSubmit={removeContestants} />
    );
  const dialogTitle = submitted !== undefined ? 'Remove contestants results' : 'Remove contestants';

  return (
    <div className="content-card__section">
      <Button
        className="contest-contestant-dialog-button"
        intent={Intent.DANGER}
        icon={<Trash />}
        onClick={toggleDialog}
        disabled={isDialogOpen}
      >
        Remove contestants
      </Button>
      <Dialog
        className="contest-contestant-dialog"
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
