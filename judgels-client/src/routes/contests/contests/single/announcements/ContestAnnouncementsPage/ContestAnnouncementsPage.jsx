import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { askDesktopNotificationPermission } from '../../../../../../modules/notification/notification';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { contestAnnouncementsQueryOptions } from '../../../../../../modules/queries/contestAnnouncement';
import { ContestAnnouncementCard } from '../ContestAnnouncementCard/ContestAnnouncementCard';
import { ContestAnnouncementCreateDialog } from '../ContestAnnouncementCreateDialog/ContestAnnouncementCreateDialog';
import { ContestAnnouncementEditDialog } from '../ContestAnnouncementEditDialog/ContestAnnouncementEditDialog';

const PAGE_SIZE = 20;

export default function ContestAnnouncementsPage() {
  const { contestSlug } = useParams({ strict: false });
  const location = useLocation();
  const page = +(location.search.page || 1);

  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const { data: response } = useQuery(contestAnnouncementsQueryOptions(contest.jid, { page }));

  const [openEditDialogAnnouncement, setOpenEditDialogAnnouncement] = useState(undefined);

  useEffect(() => {
    askDesktopNotificationPermission();
  }, []);

  const renderAnnouncements = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data: announcements, config, profilesMap } = response;
    if (announcements.page.length === 0) {
      return (
        <p>
          <small>No announcements.</small>
        </p>
      );
    }

    const { canSupervise, canManage } = config;

    return announcements.page.map(announcement => (
      <div className="content-card__section" key={announcement.jid}>
        <ContestAnnouncementCard
          contest={contest}
          announcement={announcement}
          canSupervise={canSupervise}
          canManage={canManage}
          profile={canSupervise ? profilesMap[announcement.userJid] : undefined}
          isEditDialogOpen={!openEditDialogAnnouncement ? false : announcement.jid === openEditDialogAnnouncement.jid}
          onToggleEditDialog={toggleEditDialog}
        />
      </div>
    ));
  };

  const renderCreateDialog = () => {
    if (!response) {
      return null;
    }
    if (!response.config.canManage) {
      return null;
    }

    return <ContestAnnouncementCreateDialog contest={contest} />;
  };

  const renderEditDialog = () => {
    if (!response) {
      return null;
    }
    if (!response.config.canManage) {
      return null;
    }

    return (
      <ContestAnnouncementEditDialog
        contest={contest}
        announcement={openEditDialogAnnouncement}
        onToggleEditDialog={toggleEditDialog}
      />
    );
  };

  const toggleEditDialog = announcement => {
    setOpenEditDialogAnnouncement(announcement);
  };

  return (
    <ContentCard>
      <h3>Announcements</h3>
      <hr />
      {renderCreateDialog()}
      {renderAnnouncements()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
      {renderEditDialog()}
    </ContentCard>
  );
}
