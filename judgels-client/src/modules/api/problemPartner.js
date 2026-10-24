import { get, put } from './http';
import { baseProblemURL } from './problem';

const baseURL = problemJid => `${baseProblemURL(problemJid)}/partners`;

export const problemPartnerAPI = {
  getPartners: (token, problemJid) => {
    return get(baseURL(problemJid), token);
  },

  setPartners: (token, problemJid, data) => {
    return put(baseURL(problemJid), token, data);
  },
};
