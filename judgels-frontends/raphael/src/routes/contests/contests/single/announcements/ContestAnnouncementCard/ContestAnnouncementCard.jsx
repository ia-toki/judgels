import { Button, Callout, Intent, Tag } from '@blueprintjs/core';

import { FormattedRelative } from '../../../../../../components/FormattedRelative/FormattedRelative';
import { FormattedContent } from '../../../../../../components/FormattedContent/FormattedContent';
import { UserRef } from '../../../../../../components/UserRef/UserRef';
import { ContestAnnouncementStatus } from '../../../../../../modules/api/uriel/contestAnnouncement';

import './ContestAnnouncementCard.css';

export function ContestAnnouncementCard({
  contest,
  announcement,
  canManage,
  canSupervise,
  profile,
  isEditDialogOpen,
  onToggleEditDialog,
}) {
  const isDraft = announcement.status === ContestAnnouncementStatus.Draft;
  const intent = isDraft ? Intent.DANGER : Intent.NONE;

  const user = canSupervise && (
    <>
      by <UserRef profile={profile} />
    </>
  );

  const renderEditDialog = () => {
    return (
      canManage && (
        <Button small onClick={toggleEditDialog} disabled={isEditDialogOpen}>
          Edit
        </Button>
      )
    );
  };

  const toggleEditDialog = () => {
    onToggleEditDialog(announcement);
  };

  return (
    <Callout className="contest-announcement-card" title={announcement.title} intent={intent} icon={null}>
      {isDraft ? <Tag intent={intent}>Draft</Tag> : null}
      <p className="contest-announcement-card__info">
        <small>
          published <FormattedRelative value={announcement.updatedTime} /> {user} {renderEditDialog()}
        </small>
      </p>
      <div className="clearfix" />
      <hr />
      <FormattedContent context={{ contestJid: contest.jid }}>{announcement.content}</FormattedContent>
    </Callout>
  );
}
