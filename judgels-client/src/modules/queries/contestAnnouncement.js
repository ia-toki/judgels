import { queryOptions } from '@tanstack/react-query';

import { contestAnnouncementAPI } from '../api/uriel/contestAnnouncement';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestAnnouncementsQueryOptions = (contestJid, params) => {
  const { page } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'announcements', ...[params ? [params] : []]],
    queryFn: () => contestAnnouncementAPI.getAnnouncements(getToken(), contestJid, page),
  });
};

export const createContestAnnouncementMutationOptions = contestJid => ({
  mutationFn: data => contestAnnouncementAPI.createAnnouncement(getToken(), contestJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(contestAnnouncementsQueryOptions(contestJid));
  },
});

export const updateContestAnnouncementMutationOptions = (contestJid, announcementJid) => ({
  mutationFn: async data => contestAnnouncementAPI.updateAnnouncement(getToken(), contestJid, announcementJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(contestAnnouncementsQueryOptions(contestJid));
  },
});
