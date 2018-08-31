import { Button, Dialog, Intent } from '@blueprintjs/core';
import * as React from 'react';

import { Contest } from 'modules/api/uriel/contest';

export interface ContestEditDialogProps {
  contest: Contest;
  isAllowedToEditContest: boolean;
  isEditingContest: boolean;
  onSetNotEditingContest: () => void;
}

interface ContestEditDialogState {
  isDialogOpen?: boolean;
}

export class ContestEditDialog extends React.Component<ContestEditDialogProps, ContestEditDialogState> {
  state: ContestEditDialogState = {};

  async componentDidMount() {
    if (this.props.isEditingContest) {
      this.setState({ isDialogOpen: true });
      this.props.onSetNotEditingContest();
    }
  }

  render() {
    return (
      <>
        {this.renderButton()}
        {this.renderDialog()}
      </>
    );
  }

  private renderButton = () => {
    if (!this.props.isAllowedToEditContest) {
      return null;
    }
    return (
      <Button intent={Intent.PRIMARY} icon="cog" onClick={this.toggleDialog} disabled={this.state.isDialogOpen}>
        Settings
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  private renderDialog = () => {
    return (
      <Dialog
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title="Contest settings"
        canOutsideClickClose={false}
      >
        <div className="bp3-dialog-body">WIP</div>
        <div className="bp3-dialog-footer">
          <div className="bp3-dialog-footer-actions">
            <Button text="Cancel" onClick={this.toggleDialog} />
          </div>
        </div>
      </Dialog>
    );
  };
}
