import { Classes, Button, Dialog, Intent } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import { SupervisorManagementPermission } from '../../../../../../modules/api/uriel/contestSupervisor';
import ContestSupervisorAddForm from '../ContestSupervisorAddForm/ContestSupervisorAddForm';
import { ContestSupervisorAddResultTable } from '../ContestSupervisorAddResultTable/ContestSupervisorAddResultTable';

export class ContestSupervisorAddDialog extends React.Component {
  state = {
    isDialogOpen: false,
    submitted: undefined,
  };

  render() {
    return (
      <div className="content-card__section">
        {this.renderButton()}
        {this.renderDialog()}
      </div>
    );
  }

  renderButton = () => {
    return (
      <Button
        className="contest-supervisor-dialog-button"
        intent={Intent.PRIMARY}
        icon="plus"
        onClick={this.toggleDialog}
        disabled={this.state.isDialogOpen}
      >
        Add/update supervisors
      </Button>
    );
  };

  toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  renderDialog = () => {
    const dialogBody =
      this.state.submitted !== undefined ? this.renderDialogAddResultTable() : this.renderDialogAddForm();
    const dialogTitle =
      this.state.submitted !== undefined ? 'Add/update supervisors results' : 'Add/update supervisors';

    return (
      <Dialog
        className="contest-supervisor-dialog"
        isOpen={this.state.isDialogOpen}
        onClose={this.toggleDialog}
        title={dialogTitle}
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        {dialogBody}
      </Dialog>
    );
  };

  renderDialogAddForm = () => {
    const props = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.addSupervisors,
    };
    return <ContestSupervisorAddForm {...props} />;
  };

  renderDialogAddResultTable = () => {
    const { usernames, response } = this.state.submitted;
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
            <Button text="Done" intent={Intent.PRIMARY} onClick={this.toggleDialog} />
          </div>
        </div>
      </>
    );
  };

  renderDialogForm = (fields, submitButton) => (
    <>
      <div className={classNames(Classes.DIALOG_BODY, 'contest-supervisor-dialog-body')}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={this.toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  getPermissionList(managementPermissions) {
    return !managementPermissions
      ? []
      : Object.keys(managementPermissions)
          .filter(p => managementPermissions[p])
          .map(p => SupervisorManagementPermission[p]);
  }

  addSupervisors = async dataForm => {
    const usernames = dataForm.usernames
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0);
    const data = {
      usernames,
      managementPermissions: this.getPermissionList(dataForm.managementPermissions),
    };
    const response = await this.props.onUpsertSupervisors(this.props.contest.jid, data);
    if (usernames.length !== Object.keys(response.upsertedSupervisorProfilesMap).length) {
      this.setState({ submitted: { usernames, response } });
    } else {
      this.setState({ isDialogOpen: false });
    }
  };
}
