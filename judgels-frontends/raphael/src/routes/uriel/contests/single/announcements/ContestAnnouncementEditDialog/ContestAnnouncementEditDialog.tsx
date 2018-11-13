import { Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { ContestAnnouncementData, ContestAnnouncement } from 'modules/api/uriel/contestAnnouncement';
import { Contest } from 'modules/api/uriel/contest';

import ContestAnnouncementEditForm from '../ContestAnnouncementEditForm/ContestAnnouncementEditForm';

import './ContestAnnouncementEditDialog.css';

export interface ContestAnnouncementEditDialogProps {
  contest: Contest;
  announcement?: ContestAnnouncement;
  onToggleEditDialog: () => void;
  onUpdateAnnouncement: (contestJid: string, announcementJid: string, data: ContestAnnouncementData) => void;
}

export class ContestAnnouncementEditDialog extends React.Component<ContestAnnouncementEditDialogProps> {
  render() {
    const { announcement } = this.props;

    const props = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.updateAnnouncement,
      initialValues: announcement && {
        title: announcement.title,
        content: announcement.content,
        status: announcement.status,
      },
    };

    return (
      <Dialog
        className="contest-announcement-edit-dialog"
        isOpen={!!this.props.announcement}
        onClose={this.closeDialog}
        title="Edit announcement"
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        <ContestAnnouncementEditForm {...props} />
      </Dialog>
    );
  }

  private renderDialogForm = (fields: JSX.Element, submitButton: JSX.Element) => (
    <>
      <div className="bp3-dialog-body contest-announcement-edit-dialog-body">{fields}</div>
      <div className="bp3-dialog-footer">
        <div className="bp3-dialog-footer-actions">
          <Button text="Cancel" onClick={this.closeDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  private updateAnnouncement = async (data: ContestAnnouncementData) => {
    await this.props.onUpdateAnnouncement(this.props.contest.jid, this.props.announcement!.jid, data);
    this.closeDialog();
  };

  private closeDialog = () => {
    this.props.onToggleEditDialog();
  };
}
