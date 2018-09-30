import { APP_CONFIG } from 'conf';
import { get, post } from 'modules/api/http';
import { ProfilesMap } from 'modules/api/jophiel/profile';

export interface ContestAnnouncement {
  id: number;
  jid: string;
  userJid: string;
  title: string;
  content: string;
  status: string;
  updatedTime: number;
}

export interface ContestAnnouncementConfig {
  isAllowedToCreateAnnouncement: boolean;
}

export enum ContestAnnouncementStatus {
  Draft = 'DRAFT',
  Published = 'PUBLISHED',
}

export interface ContestAnnouncementData {
  title: string;
  content: string;
  status: ContestAnnouncementStatus;
}

export interface ContestAnnouncementsResponse {
  data: ContestAnnouncement[];
  profilesMap: ProfilesMap;
}

export function createContestAnnouncementAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getAnnouncements: (token: string, contestJid: string): Promise<ContestAnnouncement[]> => {
      return get(`${baseURL}/${contestJid}/announcements`, token);
    },

    getAnnouncementConfig: (token: string, contestJid: string): Promise<ContestAnnouncementConfig> => {
      return get(`${baseURL}/${contestJid}/announcements/config`, token);
    },

    createAnnouncement: (
      token: string,
      contestJid: string,
      data: ContestAnnouncementData
    ): Promise<ContestAnnouncement> => {
      return post(`${baseURL}/${contestJid}/announcements/`, token, data);
    },
  };
}
