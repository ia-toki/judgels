import { Classes, Intent, Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { Contest, ContestCreateData } from '../../../../modules/api/uriel/contest';

import ContestCreateForm from '../ContestCreateForm/ContestCreateForm';

interface ContestCreateDialogProps {
  onCreateContest: (data: ContestCreateData) => Promise<Contest>;
}

interface ContestCreateDialogState {
  isDialogOpen?: boolean;
}

export class ContestCreateDialog extends React.Component<ContestCreateDialogProps, ContestCreateDialogState> {
  state: ContestCreateDialogState = {};

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
        New contest
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  private renderDialog = () => {
    const props: any = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.createContest,
    };
    return (
      <Dialog
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title="Create new contest"
        canOutsideClickClose={false}
      >
        <ContestCreateForm {...props} />
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

  private createContest = async (data: ContestCreateData) => {
    await this.props.onCreateContest(data);
    this.setState({ isDialogOpen: false });
  };
}
