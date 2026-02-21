import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import classNames from 'classnames';
import { useState } from 'react';

import { upsertContestManagersMutationOptions } from '../../../../../../modules/queries/contestManager';
import ContestManagerAddForm from '../ContestManagerAddForm/ContestManagerAddForm';
import { ContestManagerAddResultTable } from '../ContestManagerAddResultTable/ContestManagerAddResultTable';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export function ContestManagerAddDialog({ contest }) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [submitted, setSubmitted] = useState(undefined);

  const upsertManagersMutation = useMutation(upsertContestManagersMutationOptions(contest.jid));

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

  const addManagers = async data => {
    const usernames = data.usernames
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0);
    const response = await upsertManagersMutation.mutateAsync(usernames);
    if (usernames.length === Object.keys(response.insertedManagerProfilesMap).length) {
      toastActions.showSuccessToast('Managers added.');
      setIsDialogOpen(false);
    } else {
      setSubmitted({ usernames, response });
    }
  };

  const renderDialogAddResultTable = () => {
    const { usernames, response } = submitted;
    const { insertedManagerProfilesMap, alreadyManagerProfilesMap } = response;
    return (
      <>
        <div className={classNames(Classes.DIALOG_BODY, 'contest-manager-dialog-result-body')}>
          <ContestManagerAddResultTable
            usernames={usernames}
            insertedManagerProfilesMap={insertedManagerProfilesMap}
            alreadyManagerProfilesMap={alreadyManagerProfilesMap}
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
      renderDialogAddResultTable()
    ) : (
      <ContestManagerAddForm renderFormComponents={renderDialogForm} onSubmit={addManagers} />
    );
  const dialogTitle = submitted !== undefined ? 'Add managers results' : 'Add managers';

  return (
    <div className="content-card__section">
      <Button
        className="contest-manager-dialog-button"
        intent={Intent.PRIMARY}
        icon={<Plus />}
        onClick={toggleDialog}
        disabled={isDialogOpen}
      >
        Add managers
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
