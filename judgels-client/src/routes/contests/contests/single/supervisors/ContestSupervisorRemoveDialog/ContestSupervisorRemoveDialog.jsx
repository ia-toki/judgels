import { Classes, Button, Dialog, Intent } from '@blueprintjs/core';
import { Trash } from '@blueprintjs/icons';
import classNames from 'classnames';
import { Component } from 'react';

import ContestSupervisorRemoveForm from '../ContestSupervisorRemoveForm/ContestSupervisorRemoveForm';
import { ContestSupervisorRemoveResultTable } from '../ContestSupervisorRemoveResultTable/ContestSupervisorRemoveResultTable';

export class ContestSupervisorRemoveDialog extends Component {
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
        intent={Intent.DANGER}
        icon={<Trash />}
        onClick={this.toggleDialog}
        disabled={this.state.isDialogOpen}
      >
        Remove supervisors
      </Button>
    );
  };

  toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  renderDialog = () => {
    const dialogBody =
      this.state.submitted !== undefined ? this.renderDialogRemoveResultTable() : this.renderDialogRemoveForm();
    const dialogTitle = this.state.submitted !== undefined ? 'Remove supervisors results' : 'Remove supervisors';

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

  renderDialogRemoveForm = () => {
    const props = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.addSupervisors,
    };
    return <ContestSupervisorRemoveForm {...props} />;
  };

  renderDialogRemoveResultTable = () => {
    const { usernames, response } = this.state.submitted;
    const { deletedSupervisorProfilesMap } = response;
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

  addSupervisors = async data => {
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
