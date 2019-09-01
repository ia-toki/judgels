import { Classes, Button, Dialog, Intent } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import { ContestAnnouncementData } from '../../../../../../modules/api/uriel/contestAnnouncement';
import { Contest } from '../../../../../../modules/api/uriel/contest';

import ContestAnnouncementCreateForm from '../ContestAnnouncementCreateForm/ContestAnnouncementCreateForm';

import './ContestAnnouncementCreateDialog.css';

export interface ContestAnnouncementCreateDialogProps {
  contest: Contest;
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
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.createAnnouncement,
    };
    return (
      <Dialog
        className="contest-announcement-create-dialog"
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title="Create new announcement"
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        <ContestAnnouncementCreateForm {...props} />
      </Dialog>
    );
  };

  private renderDialogForm = (fields: JSX.Element, submitButton: JSX.Element) => (
    <>
      <div className={classNames(Classes.DIALOG_BODY, 'contest-announcement-create-dialog-body')}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={this.toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  private createAnnouncement = async (data: ContestAnnouncementData) => {
    await this.props.onCreateAnnouncement(this.props.contest.jid, data);
    this.setState({ isDialogOpen: false });
  };
}
