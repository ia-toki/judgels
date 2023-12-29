import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import classNames from 'classnames';
import { Component } from 'react';

import ContestContestantAddForm from '../ContestContestantAddForm/ContestContestantAddForm';
import { ContestContestantAddResultTable } from '../ContestContestantAddResultTable/ContestContestantAddResultTable';

export class ContestContestantAddDialog extends Component {
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
        className="contest-contestant-dialog-button"
        intent={Intent.PRIMARY}
        icon={<Plus />}
        onClick={this.toggleDialog}
        disabled={this.state.isDialogOpen}
      >
        Add contestants
      </Button>
    );
  };

  toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  renderDialog = () => {
    const dialogBody =
      this.state.submitted !== undefined ? this.renderDialogAddResultTable() : this.renderDialogAddForm();
    const dialogTitle = this.state.submitted !== undefined ? 'Add contestants results' : 'Add contestants';

    return (
      <Dialog
        className="contest-contestant-dialog"
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
      onSubmit: this.addContestants,
    };
    return <ContestContestantAddForm {...props} />;
  };

  renderDialogAddResultTable = () => {
    const { usernames, response } = this.state.submitted;
    const { insertedContestantProfilesMap, alreadyContestantProfilesMap } = response;
    return (
      <>
        <div className={classNames(Classes.DIALOG_BODY, 'contest-contestant-dialog-result-body')}>
          <ContestContestantAddResultTable
            usernames={usernames}
            insertedContestantProfilesMap={insertedContestantProfilesMap}
            alreadyContestantProfilesMap={alreadyContestantProfilesMap}
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
      <div className={classNames(Classes.DIALOG_BODY, 'contest-contestant-dialog-body')}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={this.toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  addContestants = async data => {
    const usernames = data.usernames
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0);
    const response = await this.props.onUpsertContestants(this.props.contest.jid, usernames);
    if (usernames.length !== Object.keys(response.insertedContestantProfilesMap).length) {
      this.setState({ submitted: { usernames, response } });
    } else {
      this.setState({ isDialogOpen: false });
    }
  };
}
