import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import classNames from 'classnames';
import { useState } from 'react';

import ContestContestantAddForm from '../ContestContestantAddForm/ContestContestantAddForm';
import { ContestContestantAddResultTable } from '../ContestContestantAddResultTable/ContestContestantAddResultTable';

export function ContestContestantAddDialog({ contest, onUpsertContestants }) {
  const [state, setState] = useState({
    isDialogOpen: false,
    submitted: undefined,
  });

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
      <Button
        className="contest-contestant-dialog-button"
        intent={Intent.PRIMARY}
        icon={<Plus />}
        onClick={toggleDialog}
        disabled={state.isDialogOpen}
      >
        Add contestants
      </Button>
    );
  };

  const toggleDialog = () => {
    setState(prevState => ({ ...prevState, isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  const renderDialog = () => {
    const dialogBody = state.submitted !== undefined ? renderDialogAddResultTable() : renderDialogAddForm();
    const dialogTitle = state.submitted !== undefined ? 'Add contestants results' : 'Add contestants';

    return (
      <Dialog
        className="contest-contestant-dialog"
        isOpen={state.isDialogOpen}
        onClose={toggleDialog}
        title={dialogTitle}
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        {dialogBody}
      </Dialog>
    );
  };

  const renderDialogAddForm = () => {
    const props = {
      renderFormComponents: renderDialogForm,
      onSubmit: addContestants,
    };
    return <ContestContestantAddForm {...props} />;
  };

  const renderDialogAddResultTable = () => {
    const { usernames, response } = state.submitted;
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
    const response = await onUpsertContestants(contest.jid, usernames);
    if (usernames.length !== Object.keys(response.insertedContestantProfilesMap).length) {
      setState(prevState => ({ ...prevState, submitted: { usernames, response } }));
    } else {
      setState(prevState => ({ ...prevState, isDialogOpen: false }));
    }
  };

  return render();
}
