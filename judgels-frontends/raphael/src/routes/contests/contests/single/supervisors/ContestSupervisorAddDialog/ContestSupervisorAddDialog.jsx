import { Classes, Button, Dialog, Intent } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import { Contest } from '../../../../../../modules/api/uriel/contest';
import { SupervisorManagementPermission } from '../../../../../../modules/api/uriel/contestSupervisor';
import {
  ContestSupervisorsUpsertResponse,
  ContestSupervisorUpsertData,
} from '../../../../../../modules/api/uriel/contestSupervisor';

import ContestSupervisorAddForm, {
  ContestSupervisorAddFormData,
} from '../ContestSupervisorAddForm/ContestSupervisorAddForm';
import { ContestSupervisorAddResultTable } from '../ContestSupervisorAddResultTable/ContestSupervisorAddResultTable';

export interface ContestSupervisorAddDialogProps {
  contest: Contest;
  onUpsertSupervisors: (
    contestJid: string,
    data: ContestSupervisorUpsertData
  ) => Promise<ContestSupervisorsUpsertResponse>;
}

interface ContestSupervisorAddDialogState {
  isDialogOpen?: boolean;
  submitted?: {
    usernames: string[];
    response: ContestSupervisorsUpsertResponse;
  };
}

export class ContestSupervisorAddDialog extends React.Component<
  ContestSupervisorAddDialogProps,
  ContestSupervisorAddDialogState
> {
  state: ContestSupervisorAddDialogState = {};

  render() {
    return (
      <div className="content-card__section">
        {this.renderButton()}
        {this.renderDialog()}
      </div>
    );
  }

  private renderButton = () => {
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

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  private renderDialog = () => {
    const dialogBody =
      this.state.submitted !== undefined ? this.renderDialogAddResultTable() : this.renderDialogAddForm();
    const dialogTitle =
      this.state.submitted !== undefined ? 'Add/update supervisors results' : 'Add/update supervisors';

    return (
      <Dialog
        className="contest-supervisor-dialog"
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title={dialogTitle}
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        {dialogBody}
      </Dialog>
    );
  };

  private renderDialogAddForm = () => {
    const props: any = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.addSupervisors,
    };
    return <ContestSupervisorAddForm {...props} />;
  };

  private renderDialogAddResultTable = () => {
    const { usernames, response } = this.state.submitted!;
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

  private renderDialogForm = (fields: JSX.Element, submitButton: JSX.Element) => (
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

  private getPermissionList(managementPermissions) {
    return !managementPermissions
      ? []
      : Object.keys(managementPermissions)
          .filter(p => managementPermissions[p])
          .map(p => SupervisorManagementPermission[p]);
  }

  private addSupervisors = async (dataForm: ContestSupervisorAddFormData) => {
    const usernames = dataForm.usernames
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0);
    const data: ContestSupervisorUpsertData = {
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
