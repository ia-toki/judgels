import { Button, Dialog, Icon, Intent, Tab, Tabs } from '@blueprintjs/core';
import * as React from 'react';

import { Contest } from 'modules/api/uriel/contest';

import ContestEditGeneralTab from '../ContestEditGeneralTab/ContestEditGeneralTab';
import ContestEditDescriptionTab from '../ContestEditDescriptionTab/ContestEditDescriptionTab';
import ContestEditModulesTab from '../ContestEditModulesTab/ContestEditModulesTab';

import './ContestEditDialog.css';

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
      <Button
        className="contest-edit-dialog-button"
        intent={Intent.PRIMARY}
        icon="cog"
        onClick={this.toggleDialog}
        disabled={this.state.isDialogOpen}
      >
        <span className="contest-edit-dialog-button__text">Settings</span>
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  private renderDialog = () => {
    return (
      <Dialog
        className="contest-edit-dialog"
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title="Contest settings"
        canOutsideClickClose={false}
      >
        <div className="bp3-dialog-body">
          <Tabs id="contest-edit-dialog-tabs" vertical>
            <Tab id="general" panel={<ContestEditGeneralTab />}>
              General
              <Icon icon="chevron-right" className="contest-edit-dialog__arrow" />
            </Tab>
            <Tab id="description" panel={<ContestEditDescriptionTab />}>
              Description
              <Icon icon="chevron-right" className="contest-edit-dialog__arrow" />
            </Tab>
            <Tab id="modules" panel={<ContestEditModulesTab />}>
              Modules
              <Icon icon="chevron-right" className="contest-edit-dialog__arrow" />
            </Tab>
            <Tab id="configs" panel={<div>WIP</div>}>
              Configs
              <Icon icon="chevron-right" className="contest-edit-dialog__arrow" />
            </Tab>
          </Tabs>
        </div>
        <div className="bp3-dialog-footer">
          <hr />
          <div className="bp3-dialog-footer-actions">
            <Button text="Close" onClick={this.toggleDialog} />
          </div>
        </div>
      </Dialog>
    );
  };
}
