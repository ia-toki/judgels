import { APP_CONFIG } from 'conf';
import { get } from 'modules/api/http';
import { ProfilesMap } from 'modules/api/jophiel/profile';

export interface ContestAnnouncement {
  id: number;
  jid: string;
  userJid: string;
  title: string;
  content: string;
  updatedTime: number;
}

export interface ContestAnnouncementsResponse {
  data: ContestAnnouncement[];
  profilesMap: ProfilesMap;
}

export function createContestAnnouncementAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getPublishedAnnouncements: (token: string, contestJid: string): Promise<ContestAnnouncement[]> => {
      return get(`${baseURL}/${contestJid}/announcements/published`, token);
    },
  };
}
