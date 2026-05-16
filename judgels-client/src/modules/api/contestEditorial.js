import { stringify } from 'query-string';

import { baseContestURL } from './contest';
import { get } from './http';

const baseURL = contestJid => `${baseContestURL(contestJid)}/editorial`;

export const contestEditorialAPI = {
  getEditorial: (contestJid, language) => {
    const params = stringify({ language });
    return get(`${baseURL(contestJid)}?${params}`);
  },
};
