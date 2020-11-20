import { stringify } from 'query-string';

import { get, put } from '../http';
import { baseProblemSetURL } from './problemSet';

const baseURL = problemSetJid => `${baseProblemSetURL(problemSetJid)}/problems`;

export const problemSetProblemAPI = {
  setProblems: (token, problemSetJid, data) => {
    return put(baseURL(problemSetJid), token, data);
  },

  getProblems: (token, problemSetJid) => {
    return get(baseURL(problemSetJid), token);
  },

  getProblem: (token, problemSetJid, problemAlias) => {
    return get(`${baseURL(problemSetJid)}/${problemAlias}`, token);
  },

  getProblemWorksheet: (token, problemSetJid, problemAlias, language) => {
    const params = stringify({ language });
    return get(`${baseURL(problemSetJid)}/${problemAlias}/worksheet?${params}`, token);
  },

  getProblemStats: (token, problemSetJid, problemAlias) => {
    return get(`${baseURL(problemSetJid)}/${problemAlias}/stats`, token);
  },
};
