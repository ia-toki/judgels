import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get, post, put } from '../http';

export const ContestStyle = {
  TROC: 'TROC',
  ICPC: 'ICPC',
  IOI: 'IOI',
  GCJ: 'GCJ',
  Bundle: 'BUNDLE',
};

export const ContestErrors = {
  SlugAlreadyExists: 'Uriel:ContestSlugAlreadyExists',
  ProblemSlugsNotAllowed: 'Uriel:ContestProblemSlugsNotAllowed',
  ClarificationAlreadyAnswered: 'Uriel:ClarificationAlreadyAnswered',
};

export const baseContestsURL = `${APP_CONFIG.apiUrl}/contests`;

export function baseContestURL(contestJid) {
  return `${baseContestsURL}/${contestJid}`;
}

export const contestAPI = {
  createContest: (token, data) => {
    return post(baseContestsURL, token, data);
  },

  updateContest: (token, contestJid, data) => {
    return post(`${baseContestURL(contestJid)}`, token, data);
  },

  getContests: (token, name, page) => {
    const params = stringify({ name, page });
    return get(`${baseContestsURL}?${params}`, token);
  },

  getActiveContests: token => {
    return get(`${baseContestsURL}/active`, token);
  },

  getContestBySlug: (token, contestSlug) => {
    return get(`${baseContestsURL}/slug/${contestSlug}`, token);
  },

  startVirtualContest: (token, contestJid) => {
    return post(`${baseContestURL(contestJid)}/virtual`, token);
  },

  resetVirtualContest: (token, contestJid) => {
    return put(`${baseContestURL(contestJid)}/virtual/reset`, token);
  },

  getContestDescription: (token, contestJid) => {
    return get(`${baseContestURL(contestJid)}/description`, token);
  },

  updateContestDescription: (token, contestJid, description) => {
    return post(`${baseContestURL(contestJid)}/description`, token, description);
  },
};
