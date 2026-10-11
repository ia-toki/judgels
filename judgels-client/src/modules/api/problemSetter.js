import { get, put } from './http';
import { baseProblemURL } from './problem';

const baseURL = problemJid => `${baseProblemURL(problemJid)}/setters`;

export const problemSetterAPI = {
  getSetters: (token, problemJid) => {
    return get(baseURL(problemJid), token);
  },

  setSetters: (token, problemJid, setterUsernamesMap) => {
    return put(baseURL(problemJid), token, setterUsernamesMap);
  },
};
