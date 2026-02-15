import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { callAction } from '../../../../../../modules/callAction';
import { askDesktopNotificationPermission } from '../../../../../../modules/notification/notification';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { useSession } from '../../../../../../modules/session';
import { ContestAnnouncementCard } from '../ContestAnnouncementCard/ContestAnnouncementCard';
import { ContestAnnouncementCreateDialog } from '../ContestAnnouncementCreateDialog/ContestAnnouncementCreateDialog';
import { ContestAnnouncementEditDialog } from '../ContestAnnouncementEditDialog/ContestAnnouncementEditDialog';

import * as contestAnnouncementActions from '../modules/contestAnnouncementActions';

const PAGE_SIZE = 20;

export default function ContestAnnouncementsPage() {
  const { contestSlug } = useParams({ strict: false });
  const { token } = useSession();
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));

  const [state, setState] = useState({
    response: undefined,
    lastRefreshAnnouncementsTime: 0,
    openEditDialogAnnouncement: undefined,
  });

  useEffect(() => {
    askDesktopNotificationPermission();
  }, []);

  const render = () => {
    return (
      <ContentCard>
        <h3>Announcements</h3>
        <hr />
        {renderCreateDialog()}
        {renderAnnouncements()}
        {renderPagination()}
        {renderEditDialog()}
      </ContentCard>
    );
  };

  const renderAnnouncements = () => {
    const { response, openEditDialogAnnouncement } = state;
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

  const renderPagination = () => {
    const { lastRefreshAnnouncementsTime } = state;

    return <Pagination key={lastRefreshAnnouncementsTime} pageSize={PAGE_SIZE} onChangePage={onChangePage} />;
  };

  const onChangePage = async nextPage => {
    const data = await refreshAnnouncements(nextPage);
    return data.totalCount;
  };

  const refreshAnnouncements = async page => {
    const response = await callAction(contestAnnouncementActions.getAnnouncements(contest.jid, page));
    setState(prevState => ({ ...prevState, response }));
    return response.data;
  };

  const createAnnouncement = async (contestJid, data) => {
    await callAction(contestAnnouncementActions.createAnnouncement(contestJid, data));
    setState(prevState => ({ ...prevState, lastRefreshAnnouncementsTime: new Date().getTime() }));
  };

  const updateAnnouncement = async (contestJid, announcementJid, data) => {
    await callAction(contestAnnouncementActions.updateAnnouncement(contestJid, announcementJid, data));
    setState(prevState => ({ ...prevState, lastRefreshAnnouncementsTime: new Date().getTime() }));
  };

  const renderCreateDialog = () => {
    const { response } = state;
    if (!response) {
      return null;
    }
    if (!response.config.canManage) {
      return null;
    }

    return <ContestAnnouncementCreateDialog contest={contest} onCreateAnnouncement={createAnnouncement} />;
  };

  const renderEditDialog = () => {
    const { response, openEditDialogAnnouncement } = state;
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
        onUpdateAnnouncement={updateAnnouncement}
      />
    );
  };

  const toggleEditDialog = announcement => {
    setState(prevState => ({ ...prevState, openEditDialogAnnouncement: announcement }));
  };

  return render();
}
