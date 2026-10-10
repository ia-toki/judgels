import { delete_, get, post } from './http';
import { baseProblemURL } from './problem';

const baseURL = problemJid => `${baseProblemURL(problemJid)}/versions`;

export const problemVersionAPI = {
  getVersions: (token, problemJid) => {
    return get(baseURL(problemJid), token);
  },

  restoreVersion: (token, problemJid, versionHash) => {
    return post(`${baseURL(problemJid)}/${versionHash}/restore`, token);
  },

  commitVersionLocalChanges: (token, problemJid, data) => {
    return post(`${baseURL(problemJid)}/local/commit`, token, data);
  },

  rebaseVersionLocalChanges: (token, problemJid) => {
    return post(`${baseURL(problemJid)}/local/rebase`, token);
  },

  discardVersionLocalChanges: (token, problemJid) => {
    return delete_(`${baseURL(problemJid)}/local`, token);
  },
};
