import { stringify } from 'query-string';

import { baseContestURL } from './contest';
import { get, post } from './http';

const baseURL = contestJid => `${baseContestURL(contestJid)}/managers`;

export const contestManagerAPI = {
  getManagers: (token, contestJid, page) => {
    const params = stringify({ page });
    return get(`${baseURL(contestJid)}?${params}`, token);
  },

  upsertManagers: (token, contestJid, usernames) => {
    return post(`${baseURL(contestJid)}/batch-upsert`, token, usernames);
  },

  deleteManagers: (token, contestJid, usernames) => {
    return post(`${baseURL(contestJid)}/batch-delete`, token, usernames);
  },
};
