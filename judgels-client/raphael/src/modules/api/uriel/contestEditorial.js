import { stringify } from 'query-string';

import { get } from '../http';
import { baseContestURL } from './contest';

const baseURL = contestJid => `${baseContestURL(contestJid)}/editorial`;

export const contestEditorialAPI = {
  getEditorial: (contestJid, language) => {
    const params = stringify({ language });
    return get(`${baseURL(contestJid)}?${params}`);
  },
};
