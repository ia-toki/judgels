import { stringify } from 'query-string';

import { get, post, put } from '../http';

import { baseContestURL } from './contest';

export const ContestAnnouncementStatus = {
  Draft: 'DRAFT',
  Published: 'PUBLISHED',
};

const baseURL = contestJid => `${baseContestURL(contestJid)}/announcements`;

export const contestAnnouncementAPI = {
  getAnnouncements: (token, contestJid, page) => {
    const params = stringify({ page });
    return get(`${baseURL(contestJid)}?${params}`, token);
  },

  createAnnouncement: (token, contestJid, data) => {
    return post(`${baseURL(contestJid)}`, token, data);
  },

  updateAnnouncement: (token, contestJid, announcementJid, data) => {
    return put(`${baseURL(contestJid)}/${announcementJid}`, token, data);
  },
};
