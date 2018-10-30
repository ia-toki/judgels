import { Intent, Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { Contest, ContestConfig, ContestCreateData } from 'modules/api/uriel/contest';

import ContestCreateForm from '../ContestCreateForm/ContestCreateForm';

interface ContestCreateDialogProps {
  onGetContestConfig: () => Promise<ContestConfig>;
  onCreateContest: (data: ContestCreateData) => Promise<Contest>;
}

interface ContestCreateDialogState {
  config?: ContestConfig;
  isDialogOpen?: boolean;
}

export class ContestCreateDialog extends React.Component<ContestCreateDialogProps, ContestCreateDialogState> {
  state: ContestCreateDialogState = {};

  async componentDidMount() {
    const config = await this.props.onGetContestConfig();
    this.setState({ config });
  }

  render() {
    const { config } = this.state;
    if (!config || !config.canAdminister) {
      return null;
    }

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
      <div className="bp3-dialog-body">{fields}</div>
      <div className="bp3-dialog-footer">
        <div className="bp3-dialog-footer-actions">
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
