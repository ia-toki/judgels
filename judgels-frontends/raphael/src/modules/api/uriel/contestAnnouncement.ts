import { APP_CONFIG } from 'conf';
import { get, post, put } from 'modules/api/http';

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
  canSupervise: boolean;
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
  config: ContestAnnouncementConfig;
}

export function createContestAnnouncementAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getAnnouncements: (token: string, contestJid: string): Promise<ContestAnnouncementsResponse> => {
      return get(`${baseURL}/${contestJid}/announcements`, token);
    },

    createAnnouncement: (
      token: string,
      contestJid: string,
      data: ContestAnnouncementData
    ): Promise<ContestAnnouncement> => {
      return post(`${baseURL}/${contestJid}/announcements/`, token, data);
    },

    updateAnnouncement: (
      token: string,
      contestJid: string,
      announcementJid: string,
      data: ContestAnnouncementData
    ): Promise<ContestAnnouncement> => {
      return put(`${baseURL}/${contestJid}/announcements/${announcementJid}`, token, data);
    },
  };
}
