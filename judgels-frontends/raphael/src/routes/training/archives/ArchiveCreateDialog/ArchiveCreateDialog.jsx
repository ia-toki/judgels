import { Classes, Intent, Button, Dialog } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { Component } from 'react';

import ArchiveCreateForm from '../ArchiveCreateForm/ArchiveCreateForm';

export class ArchiveCreateDialog extends Component {
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
        New archive
      </Button>
    );
  };

  toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  renderDialog = () => {
    const props = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.createArchive,
    };
    return (
      <Dialog
        isOpen={this.state.isDialogOpen}
        onClose={this.toggleDialog}
        title="Create new archive"
        canOutsideClickClose={false}
      >
        <ArchiveCreateForm {...props} />
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

  createArchive = async data => {
    await this.props.onCreateArchive(data);
    this.setState({ isDialogOpen: false });
  };
}
