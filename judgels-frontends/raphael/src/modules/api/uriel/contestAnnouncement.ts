import { get, post, put } from 'modules/api/http';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { Page } from 'modules/api/pagination';
import { stringify } from 'query-string';

import { baseContestURL } from './contest';

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
  data: Page<ContestAnnouncement>;
  config: ContestAnnouncementConfig;
  profilesMap: ProfilesMap;
}

const baseURL = (contestJid: string) => `${baseContestURL(contestJid)}/announcements`;

export const contestAnnouncementAPI = {
  getAnnouncements: (token: string, contestJid: string, page?: number): Promise<ContestAnnouncementsResponse> => {
    const params = stringify({ page });
    return get(`${baseURL(contestJid)}?${params}`, token);
  },

  createAnnouncement: (
    token: string,
    contestJid: string,
    data: ContestAnnouncementData
  ): Promise<ContestAnnouncement> => {
    return post(`${baseURL(contestJid)}`, token, data);
  },

  updateAnnouncement: (
    token: string,
    contestJid: string,
    announcementJid: string,
    data: ContestAnnouncementData
  ): Promise<ContestAnnouncement> => {
    return put(`${baseURL(contestJid)}/${announcementJid}`, token, data);
  },
};
