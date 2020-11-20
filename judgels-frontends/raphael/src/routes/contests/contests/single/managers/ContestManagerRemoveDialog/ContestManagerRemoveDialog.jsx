import { Classes, Button, Dialog, Intent } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import ContestManagerRemoveForm from '../ContestManagerRemoveForm/ContestManagerRemoveForm';
import { ContestManagerRemoveResultTable } from '../ContestManagerRemoveResultTable/ContestManagerRemoveResultTable';

export class ContestManagerRemoveDialog extends React.Component {
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

  toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  renderDialog = () => {
    const dialogBody =
      this.state.submitted !== undefined ? this.renderDialogRemoveResultTable() : this.renderDialogRemoveForm();
    const dialogTitle = this.state.submitted !== undefined ? 'Remove managers results' : 'Remove managers';

    return (
      <Dialog
        className="contest-manager-dialog"
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

  renderDialogRemoveForm = () => {
    const props = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.addManagers,
    };
    return <ContestManagerRemoveForm {...props} />;
  };

  renderDialogRemoveResultTable = () => {
    const { usernames, response } = this.state.submitted;
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

  renderDialogForm = (fields, submitButton) => (
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

  addManagers = async data => {
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
