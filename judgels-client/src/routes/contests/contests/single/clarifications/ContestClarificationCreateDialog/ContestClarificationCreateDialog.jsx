import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { Component } from 'react';

import ContestClarificationCreateForm from '../ContestClarificationCreateForm/ContestClarificationCreateForm';

export class ContestClarificationCreateDialog extends Component {
  state = {
    isDialogOpen: false,
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
      <Button intent={Intent.PRIMARY} icon={<Plus />} onClick={this.toggleDialog} disabled={this.state.isDialogOpen}>
        New clarification
      </Button>
    );
  };

  toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  renderDialog = () => {
    const { contest, problemJids, problemAliasesMap, problemNamesMap } = this.props;
    const props = {
      contestJid: contest.jid,
      problemJids,
      problemAliasesMap,
      problemNamesMap,
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.createClarification,
      initialValues: {
        topicJid: contest.jid,
      },
    };
    return (
      <Dialog
        isOpen={this.state.isDialogOpen}
        onClose={this.toggleDialog}
        title="Submit new clarification"
        canOutsideClickClose={false}
      >
        <ContestClarificationCreateForm {...props} />
      </Dialog>
    );
  };

  renderDialogForm = (fields, submitButton) => (
    <>
      <div className={Classes.DIALOG_BODY}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={this.toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  createClarification = async data => {
    await this.props.onCreateClarification(this.props.contest.jid, data);
    this.setState({ isDialogOpen: false });
  };
}
