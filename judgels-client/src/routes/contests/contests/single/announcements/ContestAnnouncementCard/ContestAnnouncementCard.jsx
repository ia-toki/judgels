import { Button, Callout, Intent, Tag } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';

import { FormattedContent } from '../../../../../../components/FormattedContent/FormattedContent';
import { FormattedRelative } from '../../../../../../components/FormattedRelative/FormattedRelative';
import { UserRef } from '../../../../../../components/UserRef/UserRef';
import { ContestAnnouncementStatus } from '../../../../../../modules/api/contestAnnouncement';

import './ContestAnnouncementCard.scss';

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
    <Callout role="article" className="contest-announcement-card" intent={intent} icon={null}>
      <Flex justifyContent="space-between" alignItems="center" gap={2}>
        <h4>{announcement.title}</h4>
        <Flex alignItems="center" gap={2}>
          {isDraft && <Tag intent={intent}>Draft</Tag>}
          <small>
            published <FormattedRelative value={announcement.updatedTime} /> {user}
          </small>
          {renderEditDialog()}
        </Flex>
      </Flex>
      <hr />
      <FormattedContent context={{ contestJid: contest.jid }}>{announcement.content}</FormattedContent>
    </Callout>
  );
}
