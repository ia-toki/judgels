import { stringify } from 'query-string';

import { get } from '../http';
import { baseContestURL } from './contest';

const baseURL = contestJid => `${baseContestURL(contestJid)}/logs`;

export const contestLogAPI = {
  getLogs: (token, contestJid, username, problemAlias, page) => {
    const params = stringify({ username, problemAlias, page });
    return get(`${baseURL(contestJid)}?${params}`, token);
  },
};
