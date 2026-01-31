import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Trash } from '@blueprintjs/icons';
import classNames from 'classnames';
import { useState } from 'react';

import ContestManagerRemoveForm from '../ContestManagerRemoveForm/ContestManagerRemoveForm';
import { ContestManagerRemoveResultTable } from '../ContestManagerRemoveResultTable/ContestManagerRemoveResultTable';

export function ContestManagerRemoveDialog({ contest, onDeleteManagers }) {
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
        className="contest-manager-dialog-button"
        intent={Intent.DANGER}
        icon={<Trash />}
        onClick={toggleDialog}
        disabled={state.isDialogOpen}
      >
        Remove managers
      </Button>
    );
  };

  const toggleDialog = () => {
    setState(prevState => ({ ...prevState, isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  const renderDialog = () => {
    const dialogBody = state.submitted !== undefined ? renderDialogRemoveResultTable() : renderDialogRemoveForm();
    const dialogTitle = state.submitted !== undefined ? 'Remove managers results' : 'Remove managers';

    return (
      <Dialog
        className="contest-manager-dialog"
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
      onSubmit: addManagers,
    };
    return <ContestManagerRemoveForm {...props} />;
  };

  const renderDialogRemoveResultTable = () => {
    const { usernames, response } = state.submitted;
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
      .filter(s => s.length > 0)
      .map(s => s.trim());
    const response = await onDeleteManagers(contest.jid, usernames);
    if (usernames.length !== Object.keys(response.deletedManagerProfilesMap).length) {
      setState(prevState => ({ ...prevState, submitted: { usernames, response } }));
    } else {
      setState(prevState => ({ ...prevState, isDialogOpen: false }));
    }
  };

  return render();
}
