import { stringify } from 'query-string';

import { get, put } from '../http';

import { baseContestURL } from './contest';

export const ContestProblemStatus = {
  Open: 'OPEN',
  Closed: 'CLOSED',
};

const baseURL = contestJid => `${baseContestURL(contestJid)}/problems`;

export const contestProblemAPI = {
  getProblems: (token, contestJid) => {
    return get(baseURL(contestJid), token);
  },

  setProblems: (token, contestJid, data) => {
    return put(baseURL(contestJid), token, data);
  },

  getProgrammingProblemWorksheet: (token, contestJid, problemAlias, language) => {
    const params = stringify({ language });
    return get(`${baseURL(contestJid)}/${problemAlias}/programming/worksheet?${params}`, token);
  },

  getBundleProblemWorksheet: (token, contestJid, problemAlias, language) => {
    const params = stringify({ language });
    return get(`${baseURL(contestJid)}/${problemAlias}/bundle/worksheet?${params}`, token);
  },
};
