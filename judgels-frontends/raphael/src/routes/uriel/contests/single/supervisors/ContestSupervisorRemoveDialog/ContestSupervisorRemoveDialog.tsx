import { Classes, Button, Dialog, Intent } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { Contest } from 'modules/api/uriel/contest';

import ContestSupervisorRemoveForm, {
  ContestSupervisorRemoveFormData,
} from '../ContestSupervisorRemoveForm/ContestSupervisorRemoveForm';
import { ContestSupervisorRemoveResultTable } from '../ContestSupervisorRemoveResultTable/ContestSupervisorRemoveResultTable';
import { ContestSupervisorsDeleteResponse } from 'modules/api/uriel/contestSupervisor';

export interface ContestSupervisorRemoveDialogProps {
  contest: Contest;
  onDeleteSupervisors: (contestJid: string, usernames: string[]) => Promise<ContestSupervisorsDeleteResponse>;
}

interface ContestSupervisorRemoveDialogState {
  isDialogOpen?: boolean;
  submitted?: {
    usernames: string[];
    response: ContestSupervisorsDeleteResponse;
  };
}

export class ContestSupervisorRemoveDialog extends React.Component<
  ContestSupervisorRemoveDialogProps,
  ContestSupervisorRemoveDialogState
> {
  state: ContestSupervisorRemoveDialogState = {};

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
        intent={Intent.DANGER}
        icon="trash"
        onClick={this.toggleDialog}
        disabled={this.state.isDialogOpen}
      >
        Remove supervisors
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  private renderDialog = () => {
    const dialogBody =
      this.state.submitted !== undefined ? this.renderDialogRemoveResultTable() : this.renderDialogRemoveForm();
    const dialogTitle = this.state.submitted !== undefined ? 'Remove supervisors results' : 'Remove supervisors';

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

  private renderDialogRemoveForm = () => {
    const props: any = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.addSupervisors,
    };
    return <ContestSupervisorRemoveForm {...props} />;
  };

  private renderDialogRemoveResultTable = () => {
    const { usernames, response } = this.state.submitted!;
    const { deletedSupervisorProfilesMap: deletedSupervisorProfilesMap } = response;
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

  private addSupervisors = async (data: ContestSupervisorRemoveFormData) => {
    const usernames = data.usernames
      .split('\n')
      .filter(s => s.length > 0)
      .map(s => s.trim());
    const response = await this.props.onDeleteSupervisors(this.props.contest.jid, usernames);
    if (usernames.length !== Object.keys(response.deletedSupervisorProfilesMap).length) {
      this.setState({ submitted: { usernames, response } });
    } else {
      this.setState({ isDialogOpen: false });
    }
  };
}
