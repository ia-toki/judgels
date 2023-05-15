import { stringify } from 'query-string';

import { get, post } from '../http';
import { baseContestURL } from './contest';

export const ContestScoreboardType = {
  Frozen: 'FROZEN',
  Official: 'OFFICIAL',
};

const baseURL = contestJid => `${baseContestURL(contestJid)}/scoreboard`;

export const contestScoreboardAPI = {
  getScoreboard: (token, contestJid, frozen, showClosedProblems, page) => {
    const params = stringify({ frozen, showClosedProblems, page });
    return get(`${baseURL(contestJid)}?${params}`, token);
  },

  refreshScoreboard: (token, contestJid) => {
    return post(`${baseURL(contestJid)}/refresh`, token);
  },
};
