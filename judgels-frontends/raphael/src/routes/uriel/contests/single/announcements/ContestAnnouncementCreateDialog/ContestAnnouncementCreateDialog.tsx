import { Button, Dialog, Intent } from '@blueprintjs/core';
import * as React from 'react';

import { ContestAnnouncementData } from 'modules/api/uriel/contestAnnouncement';
import { Contest } from 'modules/api/uriel/contest';

import ContestAnnouncementCreateForm from '../ContestAnnouncementCreateForm/ContestAnnouncementCreateForm';

export interface ContestAnnouncementCreateDialogProps {
  contest: Contest;
  onRefreshAnnouncements: () => Promise<void>;
  isAllowedToCreateAnnouncement: boolean;
  onCreateAnnouncement: (contestJid: string, data: ContestAnnouncementData) => void;
}

interface ContestAnnouncementCreateDialogState {
  isDialogOpen?: boolean;
}

export class ContestAnnouncementCreateDialog extends React.Component<
  ContestAnnouncementCreateDialogProps,
  ContestAnnouncementCreateDialogState
> {
  state: ContestAnnouncementCreateDialogState = {};

  render() {
    const { isAllowedToCreateAnnouncement } = this.props;
    if (!isAllowedToCreateAnnouncement) {
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
        New announcement
      </Button>
    );
  };

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
    await this.props.onRefreshAnnouncements();
    this.setState({ isDialogOpen: false });
  };
}
