import { Button, Callout, Intent, Tag } from '@blueprintjs/core';
import * as React from 'react';
import { FormattedRelative } from 'react-intl';

import { FormattedContent } from 'components/FormattedContent/FormattedContent';
import { UserRef } from 'components/UserRef/UserRef';
import { ContestAnnouncement, ContestAnnouncementStatus } from 'modules/api/uriel/contestAnnouncement';
import { Profile } from 'modules/api/jophiel/profile';
import { Contest } from 'modules/api/uriel/contest';

import './ContestAnnouncementCard.css';

export interface ContestAnnouncementCardProps {
  contest: Contest;
  announcement: ContestAnnouncement;
  canSupervise: boolean;
  canManage: boolean;
  profile?: Profile;
  isEditDialogOpen: boolean;
  onToggleEditDialog: (announcement?: ContestAnnouncement) => void;
}

export class ContestAnnouncementCard extends React.Component<ContestAnnouncementCardProps> {
  render() {
    const { contest, announcement, canSupervise, profile } = this.props;

    const isDraft = announcement.status === ContestAnnouncementStatus.Draft;
    const intent: Intent = isDraft ? Intent.DANGER : Intent.NONE;

    const user = canSupervise && (
      <>
        by <UserRef profile={profile!} />
      </>
    );

    return (
      <Callout className="contest-announcement-card" title={announcement.title} intent={intent} icon={null}>
        {isDraft ? <Tag intent={intent}>Draft</Tag> : null}
        <p className="contest-announcement-card__info">
          <small>
            published <FormattedRelative value={announcement.updatedTime} /> {user} {this.renderEditDialog()}
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
      this.props.canManage && (
        <Button small onClick={this.toggleEditDialog} disabled={this.props.isEditDialogOpen}>
          Edit
        </Button>
      )
    );
  };

  private toggleEditDialog = () => {
    this.props.onToggleEditDialog(this.props.announcement);
  };
}
