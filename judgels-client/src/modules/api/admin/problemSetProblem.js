import { APP_CONFIG } from '../../../conf';
import { get, put } from '../http';

const baseURL = problemSetJid => `${APP_CONFIG.apiUrl}/admin/problemsets/${problemSetJid}/problems`;

export const adminProblemSetProblemAPI = {
  getProblems: (token, problemSetJid) => {
    return get(baseURL(problemSetJid), token);
  },

  setProblems: (token, problemSetJid, data) => {
    return put(baseURL(problemSetJid), token, data);
  },
};
