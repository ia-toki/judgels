import { Classes, Intent, Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { ArchiveCreateData } from '../../../../modules/api/jerahmeel/archive';
import ArchiveCreateForm from '../ArchiveCreateForm/ArchiveCreateForm';

interface ArchiveCreateDialogProps {
  onCreateArchive: (data: ArchiveCreateData) => Promise<void>;
}

interface ArchiveCreateDialogState {
  isDialogOpen?: boolean;
}

export class ArchiveCreateDialog extends React.Component<ArchiveCreateDialogProps, ArchiveCreateDialogState> {
  state: ArchiveCreateDialogState = {};

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
      <Button intent={Intent.PRIMARY} icon="plus" onClick={this.toggleDialog} disabled={this.state.isDialogOpen}>
        New archive
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  private renderDialog = () => {
    const props: any = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.createArchive,
    };
    return (
      <Dialog
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title="Create new archive"
        canOutsideClickClose={false}
      >
        <ArchiveCreateForm {...props} />
      </Dialog>
    );
  };

  private renderDialogForm = (fields: JSX.Element, submitButton: JSX.Element) => (
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

  private createArchive = async (data: ArchiveCreateData) => {
    await this.props.onCreateArchive(data);
    this.setState({ isDialogOpen: false });
  };
}
