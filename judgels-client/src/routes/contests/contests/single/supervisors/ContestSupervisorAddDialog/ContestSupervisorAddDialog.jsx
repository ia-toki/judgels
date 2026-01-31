import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import classNames from 'classnames';
import { useState } from 'react';

import { SupervisorManagementPermission } from '../../../../../../modules/api/uriel/contestSupervisor';
import ContestSupervisorAddForm from '../ContestSupervisorAddForm/ContestSupervisorAddForm';
import { ContestSupervisorAddResultTable } from '../ContestSupervisorAddResultTable/ContestSupervisorAddResultTable';

export function ContestSupervisorAddDialog({ contest, onUpsertSupervisors }) {
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
        intent={Intent.PRIMARY}
        icon={<Plus />}
        onClick={toggleDialog}
        disabled={state.isDialogOpen}
      >
        Add/update supervisors
      </Button>
    );
  };

  const toggleDialog = () => {
    setState(prevState => ({ ...prevState, isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  const renderDialog = () => {
    const dialogBody = state.submitted !== undefined ? renderDialogAddResultTable() : renderDialogAddForm();
    const dialogTitle = state.submitted !== undefined ? 'Add/update supervisors results' : 'Add/update supervisors';

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

  const renderDialogAddForm = () => {
    const props = {
      renderFormComponents: renderDialogForm,
      onSubmit: addSupervisors,
    };
    return <ContestSupervisorAddForm {...props} />;
  };

  const renderDialogAddResultTable = () => {
    const { usernames, response } = state.submitted;
    const { upsertedSupervisorProfilesMap: insertedSupervisorProfilesMap } = response;
    return (
      <>
        <div className={classNames(Classes.DIALOG_BODY, 'contest-supervisor-dialog-result-body')}>
          <ContestSupervisorAddResultTable
            usernames={usernames}
            insertedSupervisorProfilesMap={insertedSupervisorProfilesMap}
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

  const getPermissionList = managementPermissions => {
    return !managementPermissions
      ? []
      : Object.keys(managementPermissions)
          .filter(p => managementPermissions[p])
          .map(p => SupervisorManagementPermission[p]);
  };

  const addSupervisors = async dataForm => {
    const usernames = dataForm.usernames
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0);
    const data = {
      usernames,
      managementPermissions: getPermissionList(dataForm.managementPermissions),
    };
    const response = await onUpsertSupervisors(contest.jid, data);
    if (usernames.length !== Object.keys(response.upsertedSupervisorProfilesMap).length) {
      setState(prevState => ({ ...prevState, submitted: { usernames, response } }));
    } else {
      setState(prevState => ({ ...prevState, isDialogOpen: false }));
    }
  };

  return render();
}
