import { Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { ContestAnnouncementData, ContestAnnouncement } from 'modules/api/uriel/contestAnnouncement';
import { Contest } from 'modules/api/uriel/contest';

import ContestAnnouncementEditForm from '../ContestAnnouncementEditForm/ContestAnnouncementEditForm';

export interface ContestAnnouncementEditDialogProps {
  contest: Contest;
  announcement: ContestAnnouncement;
  isAllowedToEditAnnouncement: boolean;
  onRefreshAnnouncements: () => Promise<void>;
  onUpdateAnnouncement: (contestJid: string, announcementJid: string, data: ContestAnnouncementData) => void;
}

interface ContestAnnouncementEditDialogState {
  isDialogOpen?: boolean;
}

export class ContestAnnouncementEditDialog extends React.Component<
  ContestAnnouncementEditDialogProps,
  ContestAnnouncementEditDialogState
> {
  state: ContestAnnouncementEditDialogState = {};

  render() {
    const { isAllowedToEditAnnouncement } = this.props;
    if (!isAllowedToEditAnnouncement) {
      return null;
    }

    return (
      <>
        {this.renderButton()}
        {this.renderDialog()}
      </>
    );
  }

  private renderButton = () => {
    return (
      <Button icon="edit" onClick={this.toggleDialog} disabled={this.state.isDialogOpen}>
        Edit
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  private renderDialog = () => {
    const props: any = {
      announcement: this.props.announcement,
      contestJid: this.props.contest.jid,
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.updateAnnouncement,
    };
    const initialValues: any = {
      title: props.announcement.title,
      content: props.announcement.content,
      status: props.announcement.status,
    };

    return (
      <Dialog
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title="Edit announcement"
        canOutsideClickClose={false}
      >
        <ContestAnnouncementEditForm {...props} initialValues={initialValues} />
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

  private updateAnnouncement = async (data: ContestAnnouncementData) => {
    await this.props.onUpdateAnnouncement(this.props.contest.jid, this.props.announcement.jid, data);
    await this.props.onRefreshAnnouncements();
    this.setState({ isDialogOpen: false });
  };
}
