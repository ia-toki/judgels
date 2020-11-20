import { stringify } from 'query-string';

import { delete_, get, post } from '../http';
import { baseContestURL } from './contest';

export const ContestContestantState = {
  None: 'NONE',
  Registrable: 'REGISTRABLE',
  RegistrableWrongDivision: 'REGISTRABLE_WRONG_DIVISION',
  Registrant: 'REGISTRANT',
  Contestant: 'CONTESTANT',
};

const baseURL = contestJid => `${baseContestURL(contestJid)}/contestants`;

export const contestContestantAPI = {
  getContestants: (token, contestJid, page) => {
    const params = stringify({ page });
    return get(`${baseURL(contestJid)}?${params}`, token);
  },

  getApprovedContestants: (token, contestJid) => {
    return get(`${baseURL(contestJid)}/approved`, token);
  },

  getApprovedContestantsCount: (token, contestJid) => {
    return get(`${baseURL(contestJid)}/approved/count`, token);
  },

  registerMyselfAsContestant: (token, contestJid) => {
    return post(`${baseURL(contestJid)}/me`, token);
  },

  unregisterMyselfAsContestant: (token, contestJid) => {
    return delete_(`${baseURL(contestJid)}/me`, token);
  },

  getMyContestantState: (token, contestJid) => {
    return get(`${baseURL(contestJid)}/me/state`, token);
  },

  upsertContestants: (token, contestJid, usernames) => {
    return post(`${baseURL(contestJid)}/batch-upsert`, token, usernames);
  },

  deleteContestants: (token, contestJid, usernames) => {
    return post(`${baseURL(contestJid)}/batch-delete`, token, usernames);
  },
};
