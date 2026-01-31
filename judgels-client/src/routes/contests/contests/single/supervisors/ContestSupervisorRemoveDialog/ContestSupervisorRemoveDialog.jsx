import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Trash } from '@blueprintjs/icons';
import classNames from 'classnames';
import { useState } from 'react';

import ContestSupervisorRemoveForm from '../ContestSupervisorRemoveForm/ContestSupervisorRemoveForm';
import { ContestSupervisorRemoveResultTable } from '../ContestSupervisorRemoveResultTable/ContestSupervisorRemoveResultTable';

export function ContestSupervisorRemoveDialog({ contest, onDeleteSupervisors }) {
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
        className="contest-supervisor-dialog-button"
        intent={Intent.DANGER}
        icon={<Trash />}
        onClick={toggleDialog}
        disabled={state.isDialogOpen}
      >
        Remove supervisors
      </Button>
    );
  };

  const toggleDialog = () => {
    setState(prevState => ({ ...prevState, isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  const renderDialog = () => {
    const dialogBody = state.submitted !== undefined ? renderDialogRemoveResultTable() : renderDialogRemoveForm();
    const dialogTitle = state.submitted !== undefined ? 'Remove supervisors results' : 'Remove supervisors';

    return (
      <Dialog
        className="contest-supervisor-dialog"
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
      onSubmit: addSupervisors,
    };
    return <ContestSupervisorRemoveForm {...props} />;
  };

  const renderDialogRemoveResultTable = () => {
    const { usernames, response } = state.submitted;
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

  const addSupervisors = async data => {
    const usernames = data.usernames
      .split('\n')
      .filter(s => s.length > 0)
      .map(s => s.trim());
    const response = await onDeleteSupervisors(contest.jid, usernames);
    if (usernames.length !== Object.keys(response.deletedSupervisorProfilesMap).length) {
      setState(prevState => ({ ...prevState, submitted: { usernames, response } }));
    } else {
      setState(prevState => ({ ...prevState, isDialogOpen: false }));
    }
  };

  return render();
}
