import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Trash } from '@blueprintjs/icons';
import classNames from 'classnames';
import { useState } from 'react';

import ContestContestantRemoveForm from '../ContestContestantRemoveForm/ContestContestantRemoveForm';
import { ContestContestantRemoveResultTable } from '../ContestContestantRemoveResultTable/ContestContestantRemoveResultTable';

export function ContestContestantRemoveDialog({ contest, onDeleteContestants }) {
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
        intent={Intent.DANGER}
        icon={<Trash />}
        onClick={toggleDialog}
        disabled={state.isDialogOpen}
      >
        Remove contestants
      </Button>
    );
  };

  const toggleDialog = () => {
    setState(prevState => ({ ...prevState, isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  const renderDialog = () => {
    const dialogBody = state.submitted !== undefined ? renderDialogRemoveResultTable() : renderDialogRemoveForm();
    const dialogTitle = state.submitted !== undefined ? 'Remove contestants results' : 'Remove contestants';

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

  const renderDialogRemoveForm = () => {
    const props = {
      renderFormComponents: renderDialogForm,
      onSubmit: addContestants,
    };
    return <ContestContestantRemoveForm {...props} />;
  };

  const renderDialogRemoveResultTable = () => {
    const { usernames, response } = state.submitted;
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
      .filter(s => s.length > 0)
      .map(s => s.trim());
    const response = await onDeleteContestants(contest.jid, usernames);
    if (usernames.length !== Object.keys(response.deletedContestantProfilesMap).length) {
      setState(prevState => ({ ...prevState, submitted: { usernames, response } }));
    } else {
      setState(prevState => ({ ...prevState, isDialogOpen: false }));
    }
  };

  return render();
}
