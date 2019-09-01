import { Classes, Button, Dialog, Intent } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import { Contest } from '../../../../../../modules/api/uriel/contest';
import { ContestManagersDeleteResponse } from '../../../../../../modules/api/uriel/contestManager';

import ContestManagerRemoveForm, {
  ContestManagerRemoveFormData,
} from '../ContestManagerRemoveForm/ContestManagerRemoveForm';
import { ContestManagerRemoveResultTable } from '../ContestManagerRemoveResultTable/ContestManagerRemoveResultTable';

export interface ContestManagerRemoveDialogProps {
  contest: Contest;
  onDeleteManagers: (contestJid: string, usernames: string[]) => Promise<ContestManagersDeleteResponse>;
}

interface ContestManagerRemoveDialogState {
  isDialogOpen?: boolean;
  submitted?: {
    usernames: string[];
    response: ContestManagersDeleteResponse;
  };
}

export class ContestManagerRemoveDialog extends React.Component<
  ContestManagerRemoveDialogProps,
  ContestManagerRemoveDialogState
> {
  state: ContestManagerRemoveDialogState = {};

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
        intent={Intent.DANGER}
        icon="trash"
        onClick={this.toggleDialog}
        disabled={this.state.isDialogOpen}
      >
        Remove managers
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  private renderDialog = () => {
    const dialogBody =
      this.state.submitted !== undefined ? this.renderDialogRemoveResultTable() : this.renderDialogRemoveForm();
    const dialogTitle = this.state.submitted !== undefined ? 'Remove managers results' : 'Remove managers';

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

  private renderDialogRemoveForm = () => {
    const props: any = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.addManagers,
    };
    return <ContestManagerRemoveForm {...props} />;
  };

  private renderDialogRemoveResultTable = () => {
    const { usernames, response } = this.state.submitted!;
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
            <Button text="Done" intent={Intent.PRIMARY} onClick={this.toggleDialog} />
          </div>
        </div>
      </>
    );
  };

  private renderDialogForm = (fields: JSX.Element, submitButton: JSX.Element) => (
    <>
      <div className={classNames(Classes.DIALOG_BODY, 'contest-manager-dialog-body')}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={this.toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  private addManagers = async (data: ContestManagerRemoveFormData) => {
    const usernames = data.usernames
      .split('\n')
      .filter(s => s.length > 0)
      .map(s => s.trim());
    const response = await this.props.onDeleteManagers(this.props.contest.jid, usernames);
    if (usernames.length !== Object.keys(response.deletedManagerProfilesMap).length) {
      this.setState({ submitted: { usernames, response } });
    } else {
      this.setState({ isDialogOpen: false });
    }
  };
}
