import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Trash } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import classNames from 'classnames';
import { useState } from 'react';

import { deleteContestManagersMutationOptions } from '../../../../../../modules/queries/contestManager';
import ContestManagerRemoveForm from '../ContestManagerRemoveForm/ContestManagerRemoveForm';
import { ContestManagerRemoveResultTable } from '../ContestManagerRemoveResultTable/ContestManagerRemoveResultTable';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export function ContestManagerRemoveDialog({ contest }) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [submitted, setSubmitted] = useState(undefined);

  const deleteManagersMutation = useMutation(deleteContestManagersMutationOptions(contest.jid));

  const toggleDialog = () => {
    setIsDialogOpen(open => !open);
    setSubmitted(undefined);
  };

  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={classNames(Classes.DIALOG_BODY, 'contest-manager-dialog-body')}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const removeManagers = async data => {
    const usernames = data.usernames
      .split('\n')
      .filter(s => s.length > 0)
      .map(s => s.trim());
    const response = await deleteManagersMutation.mutateAsync(usernames);
    if (usernames.length === Object.keys(response.deletedManagerProfilesMap).length) {
      toastActions.showSuccessToast('Managers removed.');
      setIsDialogOpen(false);
    } else {
      setSubmitted({ usernames, response });
    }
  };

  const renderDialogRemoveResultTable = () => {
    const { usernames, response } = submitted;
    const { deletedManagerProfilesMap } = response;
    return (
      <>
        <div className={classNames(Classes.DIALOG_BODY, 'contest-manager-dialog-result-body')}>
          <ContestManagerRemoveResultTable
            usernames={usernames}
            deletedManagerProfilesMap={deletedManagerProfilesMap}
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
      <ContestManagerRemoveForm renderFormComponents={renderDialogForm} onSubmit={removeManagers} />
    );
  const dialogTitle = submitted !== undefined ? 'Remove managers results' : 'Remove managers';

  return (
    <div className="content-card__section">
      <Button
        className="contest-manager-dialog-button"
        intent={Intent.DANGER}
        icon={<Trash />}
        onClick={toggleDialog}
        disabled={isDialogOpen}
      >
        Remove managers
      </Button>
      <Dialog
        className="contest-manager-dialog"
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
