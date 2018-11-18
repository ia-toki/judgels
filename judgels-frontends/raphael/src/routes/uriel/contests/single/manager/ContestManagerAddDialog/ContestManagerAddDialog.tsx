import { Button, Dialog, Intent } from '@blueprintjs/core';
import * as React from 'react';

import { Contest } from 'modules/api/uriel/contest';
import { ContestManagerUpsertResponse } from 'modules/api/uriel/contestManager';

import ContestManagerAddForm, { ContestManagerAddFormData } from '../ContestManagerAddForm/ContestManagerAddForm';
import { ContestManagerAddResultTable } from '../ContestManagerAddResultTable/ContestManagerAddResultTable';

export interface ContestManagerAddDialogProps {
  contest: Contest;
  onUpsertManagers: (contestJid: string, usernames: string[]) => Promise<ContestManagerUpsertResponse>;
}

interface ContestManagerAddDialogState {
  isDialogOpen?: boolean;
  submitted?: {
    usernames: string[];
    response: ContestManagerUpsertResponse;
  };
}

export class ContestManagerAddDialog extends React.Component<
  ContestManagerAddDialogProps,
  ContestManagerAddDialogState
> {
  state: ContestManagerAddDialogState = {};

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
        className="contest-manager-dialog-button"
        intent={Intent.PRIMARY}
        icon="plus"
        onClick={this.toggleDialog}
        disabled={this.state.isDialogOpen}
      >
        Add managers
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  private renderDialog = () => {
    const dialogBody =
      this.state.submitted !== undefined ? this.renderDialogAddResultTable() : this.renderDialogAddForm();
    const dialogTitle = this.state.submitted !== undefined ? 'Add managers results' : 'Add managers';

    return (
      <Dialog
        className="contest-manager-dialog"
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
      onSubmit: this.addManagers,
    };
    return <ContestManagerAddForm {...props} />;
  };

  private renderDialogAddResultTable = () => {
    const { usernames, response } = this.state.submitted!;
    const { insertedManagerProfilesMap, alreadyManagerProfilesMap } = response;
    return (
      <>
        <div className="bp3-dialog-body contest-manager-dialog-result-body">
          <ContestManagerAddResultTable
            usernames={usernames}
            insertedManagerProfilesMap={insertedManagerProfilesMap}
            alreadyManagerProfilesMap={alreadyManagerProfilesMap}
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
      <div className="bp3-dialog-body contest-manager-dialog-body">{fields}</div>
      <div className="bp3-dialog-footer">
        <div className="bp3-dialog-footer-actions">
          <Button text="Cancel" onClick={this.toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  private addManagers = async (data: ContestManagerAddFormData) => {
    const usernames = data.usernames
      .split('\n')
      .filter(s => s.length > 0)
      .map(s => s.trim());
    const response = await this.props.onUpsertManagers(this.props.contest.jid, usernames);
    if (usernames.length !== Object.keys(response.insertedManagerProfilesMap).length) {
      this.setState({ submitted: { usernames, response } });
    } else {
      this.setState({ isDialogOpen: false });
    }
  };
}
