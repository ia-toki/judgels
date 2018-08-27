import { Button, Dialog, Intent, Switch } from '@blueprintjs/core';
import * as React from 'react';

import { ContestAnnouncementConfig, ContestAnnouncementData } from 'modules/api/uriel/contestAnnouncement';
import { Contest } from 'modules/api/uriel/contest';

import ContestAnnouncementCreateForm from '../ContestAnnouncementCreateForm/ContestAnnouncementCreateForm';

export interface ContestAnnouncementCreateDialogProps {
  contest: Contest;
  onRefreshAnnouncements: (isShowDrafts: boolean) => Promise<void>;
  onGetAnnouncementConfig: (contestJid: string) => Promise<ContestAnnouncementConfig>;
  onCreateAnnouncement: (contestJid: string, data: ContestAnnouncementData) => void;
}

interface ContestAnnouncementCreateDialogState {
  config?: ContestAnnouncementConfig;
  isDialogOpen?: boolean;
  isShowDrafts: boolean;
}

export class ContestAnnouncementCreateDialog extends React.Component<
  ContestAnnouncementCreateDialogProps,
  ContestAnnouncementCreateDialogState
> {
  state: ContestAnnouncementCreateDialogState = {isShowDrafts: false};

  async componentDidMount() {
    const config = await this.props.onGetAnnouncementConfig(this.props.contest.jid);
    this.setState({ config });
  }

  render() {
    const { config } = this.state;
    if (!config) {
      return null;
    }

    return (
      <div className="content-card__section">
        {this.renderButton(config)}
        {this.renderDialog()}
      </div>
    );
  }

  private renderButton = (config: ContestAnnouncementConfig) => {
    if (!config.isAllowedToCreateAnnouncement) {
      return;
    }
    return (
      <>
        <Button intent={Intent.PRIMARY} icon="plus" onClick={this.toggleDialog} disabled={this.state.isDialogOpen}>
          New announcement
        </Button>
        <Switch className='contest-announcement-card__draft-switch' checked={this.state.isShowDrafts} label="Show drafts" onChange={this.toggleShowDrafts} />
      </>
    );
  };

  private toggleShowDrafts = async () => {
    let newState = !this.state.isShowDrafts
    this.setState(prevState => ({ isShowDrafts: !prevState.isShowDrafts }))
    await this.props.onRefreshAnnouncements(newState);
  }

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  private renderDialog = () => {
    const props: any = {
      contestJid: this.props.contest.jid,
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.createAnnouncement,
    };
    return (
      <Dialog
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title="Create new announcement"
        canOutsideClickClose={false}
      >
        <ContestAnnouncementCreateForm {...props} />
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

  private createAnnouncement = async (data: ContestAnnouncementData) => {
    await this.props.onCreateAnnouncement(this.props.contest.jid, data);
    await this.props.onRefreshAnnouncements(this.state.isShowDrafts);
    this.setState({ isDialogOpen: false });
  };
}
