import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Trash } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import classNames from 'classnames';
import { useState } from 'react';

import { removeContestManagersMutationOptions } from '../../../../../modules/queries/contestManager';
import ContestManagerRemoveForm from '../ContestManagerRemoveForm/ContestManagerRemoveForm';
import { ContestManagerRemoveResultTable } from '../ContestManagerRemoveResultTable/ContestManagerRemoveResultTable';

import * as toastActions from '../../../../../modules/toast/toastActions';

import '../../components/ContestUserDialog.scss';

export function ContestManagerRemoveDialog({ contest }) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [submitted, setSubmitted] = useState(undefined);

  const removeManagersMutation = useMutation(removeContestManagersMutationOptions(contest.jid));

  const toggleDialog = () => {
    setIsDialogOpen(open => !open);
    setSubmitted(undefined);
  };

  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={classNames(Classes.DIALOG_BODY, 'contest-user-dialog__body')}>{fields}</div>
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
    const response = await removeManagersMutation.mutateAsync(usernames);
    if (usernames.length === Object.keys(response.removedManagerProfilesMap).length) {
      toastActions.showSuccessToast('Managers removed.');
      setIsDialogOpen(false);
    } else {
      setSubmitted({ usernames, response });
    }
  };

  const renderDialogRemoveResultTable = () => {
    const { usernames, response } = submitted;
    const { removedManagerProfilesMap } = response;
    return (
      <>
        <div className={classNames(Classes.DIALOG_BODY, 'contest-user-dialog__result-body')}>
          <ContestManagerRemoveResultTable
            usernames={usernames}
            removedManagerProfilesMap={removedManagerProfilesMap}
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
    <>
      <Button small intent={Intent.DANGER} icon={<Trash />} onClick={toggleDialog} disabled={isDialogOpen}>
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
    </>
  );
}
