import { Button, Dialog, Intent } from '@blueprintjs/core';
import * as React from 'react';

import { Contest } from 'modules/api/uriel/contest';
import { ContestContestantDeleteResponse } from 'modules/api/uriel/contestContestant';

import ContestContestantRemoveForm, {
  ContestContestantRemoveFormData,
} from '../ContestContestantRemoveForm/ContestContestantRemoveForm';
import { ContestContestantRemoveResultTable } from '../ContestContestantRemoveResultTable/ContestContestantRemoveResultTable';

import './ContestContestantRemoveDialog.css';

export interface ContestContestantRemoveDialogProps {
  contest: Contest;
  onDeleteContestants: (contestJid: string, usernames: string[]) => Promise<ContestContestantDeleteResponse>;
}

interface ContestContestantRemoveDialogState {
  isDialogOpen?: boolean;
  submitted?: {
    usernames: string[];
    response: ContestContestantDeleteResponse;
  };
}

export class ContestContestantRemoveDialog extends React.Component<
  ContestContestantRemoveDialogProps,
  ContestContestantRemoveDialogState
> {
  state: ContestContestantRemoveDialogState = {};

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
        className="contest-contestant-remove-button"
        intent={Intent.DANGER}
        icon="trash"
        onClick={this.toggleDialog}
        disabled={this.state.isDialogOpen}
      >
        Remove contestants
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  private renderDialog = () => {
    const dialogBody =
      this.state.submitted !== undefined ? this.renderDialogRemoveResultTable() : this.renderDialogRemoveForm();
    const dialogTitle = this.state.submitted !== undefined ? 'Remove contestants results' : 'Remove contestants';

    return (
      <Dialog
        className="contest-contestant-remove-dialog"
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
      onSubmit: this.addContestants,
    };
    return <ContestContestantRemoveForm {...props} />;
  };

  private renderDialogRemoveResultTable = () => {
    const { usernames, response } = this.state.submitted!;
    const { deletedContestantProfilesMap } = response;
    return (
      <>
        <div className="bp3-dialog-body contest-contestant-remove-dialog-result-body">
          <ContestContestantRemoveResultTable
            usernames={usernames}
            deletedContestantProfilesMap={deletedContestantProfilesMap}
          />
        </div>
        <div className="bp3-dialog-footer">
          <div className="bp3-dialog-footer-actions">
            <Button text="Done" intent={Intent.PRIMARY} onClick={this.toggleDialog} />
          </div>
        </div>
      </>
    );
  };

  private renderDialogForm = (fields: JSX.Element, submitButton: JSX.Element) => (
    <>
      <div className="bp3-dialog-body contest-contestant-remove-dialog-body">{fields}</div>
      <div className="bp3-dialog-footer">
        <div className="bp3-dialog-footer-actions">
          <Button text="Cancel" onClick={this.toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  private addContestants = async (data: ContestContestantRemoveFormData) => {
    const usernames = data.usernames
      .split('\n')
      .filter(s => s.length > 0)
      .map(s => s.trim());
    const response = await this.props.onDeleteContestants(this.props.contest.jid, usernames);
    if (usernames.length !== Object.keys(response.deletedContestantProfilesMap).length) {
      this.setState({ submitted: { usernames, response } });
    } else {
      this.setState({ isDialogOpen: false });
    }
  };
}
