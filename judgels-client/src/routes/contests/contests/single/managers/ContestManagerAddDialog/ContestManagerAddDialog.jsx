import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import classNames from 'classnames';
import { useState } from 'react';

import ContestManagerAddForm from '../ContestManagerAddForm/ContestManagerAddForm';
import { ContestManagerAddResultTable } from '../ContestManagerAddResultTable/ContestManagerAddResultTable';

export function ContestManagerAddDialog({ contest, onUpsertManagers }) {
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
        intent={Intent.PRIMARY}
        icon={<Plus />}
        onClick={toggleDialog}
        disabled={state.isDialogOpen}
      >
        Add managers
      </Button>
    );
  };

  const toggleDialog = () => {
    setState(prevState => ({ ...prevState, isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  const renderDialog = () => {
    const dialogBody = state.submitted !== undefined ? renderDialogAddResultTable() : renderDialogAddForm();
    const dialogTitle = state.submitted !== undefined ? 'Add managers results' : 'Add managers';

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

  const renderDialogAddForm = () => {
    const props = {
      renderFormComponents: renderDialogForm,
      onSubmit: addManagers,
    };
    return <ContestManagerAddForm {...props} />;
  };

  const renderDialogAddResultTable = () => {
    const { usernames, response } = state.submitted;
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
    const response = await onUpsertManagers(contest.jid, usernames);
    if (usernames.length !== Object.keys(response.insertedManagerProfilesMap).length) {
      setState(prevState => ({ ...prevState, submitted: { usernames, response } }));
    } else {
      setState(prevState => ({ ...prevState, isDialogOpen: false }));
    }
  };

  return render();
}
