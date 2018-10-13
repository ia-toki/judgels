import { Button, Callout, Tag } from '@blueprintjs/core';
import * as React from 'react';
import { FormattedRelative } from 'react-intl';

import { FormattedContent } from 'components/FormattedContent/FormattedContent';
import { ContestAnnouncement, ContestAnnouncementStatus } from 'modules/api/uriel/contestAnnouncement';
import { Contest } from 'modules/api/uriel/contest';

import './ContestAnnouncementCard.css';

export interface ContestAnnouncementCardProps {
  announcement: ContestAnnouncement;
  isAllowedToEditAnnouncement: boolean;
  contest: Contest;
  isEditDialogOpen: boolean;
  onToggleEditDialog: (announcement?: ContestAnnouncement) => void;
}

export class ContestAnnouncementCard extends React.Component<ContestAnnouncementCardProps> {
  render() {
    const { announcement, contest } = this.props;

    return (
      <Callout className="contest-announcement-card" title={announcement.title}>
        {announcement.status === ContestAnnouncementStatus.Draft ? <Tag>Draft</Tag> : null}
        <p className="contest-announcement-card__info">
          <small>
            published <FormattedRelative value={announcement.updatedTime} /> {this.renderEditDialog()}
          </small>
        </p>
        <div className="clearfix" />
        <hr />
        <FormattedContent context={{ contestJid: contest.jid }}>{announcement.content}</FormattedContent>
      </Callout>
    );
  }

  private renderEditDialog = () => {
    return (
      this.props.isAllowedToEditAnnouncement && (
        <Button icon="edit" onClick={this.toggleEditDialog} disabled={this.props.isEditDialogOpen}>
          Edit
        </Button>
      )
    );
  };

  private toggleEditDialog = () => {
    this.props.onToggleEditDialog(this.props.announcement);
  };
}
