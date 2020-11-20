import { stringify } from 'query-string';

import { get, post, put } from '../http';
import { baseContestURL } from './contest';

export const ContestClarificationStatus = {
  Asked: 'ASKED',
  Answered: 'ANSWERED',
};

const baseURL = contestJid => `${baseContestURL(contestJid)}/clarifications`;

export const contestClarificationAPI = {
  createClarification: (token, contestJid, data) => {
    return post(`${baseURL(contestJid)}`, token, data);
  },

  getClarifications: (token, contestJid, status, language, page) => {
    const params = stringify({ status, language, page });
    return get(`${baseURL(contestJid)}?${params}`, token);
  },

  answerClarification: (token, contestJid, clarificationJid, data) => {
    return put(`${baseURL(contestJid)}/${clarificationJid}/answer`, token, data);
  },
};
