import { Button, Classes, Dialog, Intent, Tab, Tabs } from '@blueprintjs/core';
import { ChevronRight, Cog } from '@blueprintjs/icons';
import { Component } from 'react';

import ContestEditConfigsTab from '../ContestEditConfigsTab/ContestEditConfigsTab';
import ContestEditDescriptionTab from '../ContestEditDescriptionTab/ContestEditDescriptionTab';
import ContestEditGeneralTab from '../ContestEditGeneralTab/ContestEditGeneralTab';
import ContestEditModulesTab from '../ContestEditModulesTab/ContestEditModulesTab';

import './ContestEditDialog.scss';

export class ContestEditDialog extends Component {
  state = {
    isDialogOpen: false,
  };

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

  renderButton = () => {
    if (!this.props.canManage) {
      return null;
    }
    return (
      <Button
        className="contest-edit-dialog-button"
        intent={Intent.PRIMARY}
        icon={<Cog />}
        onClick={this.toggleDialog}
        disabled={this.state.isDialogOpen}
      >
        <span className="contest-edit-dialog-button__text">Settings</span>
      </Button>
    );
  };

  toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  renderDialog = () => {
    return (
      <Dialog
        className="contest-edit-dialog"
        isOpen={this.state.isDialogOpen}
        onClose={this.toggleDialog}
        title="Contest settings"
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        <div className={Classes.DIALOG_BODY}>
          <Tabs id="contest-edit-dialog-tabs" vertical renderActiveTabPanelOnly animate={false}>
            <Tab id="general" panel={<ContestEditGeneralTab />}>
              <span>General</span>
              <ChevronRight className="contest-edit-dialog__arrow" />
            </Tab>
            <Tab id="description" panel={<ContestEditDescriptionTab />}>
              <span>Description</span>
              <ChevronRight className="contest-edit-dialog__arrow" />
            </Tab>
            <Tab id="modules" panel={<ContestEditModulesTab />}>
              <span>Modules</span>
              <ChevronRight className="contest-edit-dialog__arrow" />
            </Tab>
            <Tab id="configs" panel={<ContestEditConfigsTab />}>
              <span>Configs</span>
              <ChevronRight className="contest-edit-dialog__arrow" />
            </Tab>
          </Tabs>
        </div>
        <div className={Classes.DIALOG_FOOTER}>
          <hr />
          <div className={Classes.DIALOG_FOOTER_ACTIONS}>
            <Button text="Close" onClick={this.toggleDialog} />
          </div>
        </div>
      </Dialog>
    );
  };
}
