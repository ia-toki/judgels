import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import classNames from 'classnames';
import { useState } from 'react';

import { upsertContestContestantsMutationOptions } from '../../../../../../modules/queries/contestContestant';
import ContestContestantAddForm from '../ContestContestantAddForm/ContestContestantAddForm';
import { ContestContestantAddResultTable } from '../ContestContestantAddResultTable/ContestContestantAddResultTable';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export function ContestContestantAddDialog({ contest }) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [submitted, setSubmitted] = useState(undefined);

  const upsertContestantsMutation = useMutation(upsertContestContestantsMutationOptions(contest.jid));

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

  const addContestants = async data => {
    const usernames = data.usernames
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0);
    const response = await upsertContestantsMutation.mutateAsync(usernames);
    if (usernames.length === Object.keys(response.insertedContestantProfilesMap).length) {
      toastActions.showSuccessToast('Contestants added.');
      setIsDialogOpen(false);
    } else {
      setSubmitted({ usernames, response });
    }
  };

  const renderDialogAddResultTable = () => {
    const { usernames, response } = submitted;
    const { insertedContestantProfilesMap, alreadyContestantProfilesMap } = response;
    return (
      <>
        <div className={classNames(Classes.DIALOG_BODY, 'contest-contestant-dialog-result-body')}>
          <ContestContestantAddResultTable
            usernames={usernames}
            insertedContestantProfilesMap={insertedContestantProfilesMap}
            alreadyContestantProfilesMap={alreadyContestantProfilesMap}
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
      <ContestContestantAddForm renderFormComponents={renderDialogForm} onSubmit={addContestants} />
    );
  const dialogTitle = submitted !== undefined ? 'Add contestants results' : 'Add contestants';

  return (
    <div className="content-card__section">
      <Button
        className="contest-contestant-dialog-button"
        intent={Intent.PRIMARY}
        icon={<Plus />}
        onClick={toggleDialog}
        disabled={isDialogOpen}
      >
        Add contestants
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
