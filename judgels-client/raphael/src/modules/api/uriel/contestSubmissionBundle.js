import { stringify } from 'query-string';

import { get, post } from '../http';
import { baseContestsURL } from './contest';

const baseURL = `${baseContestsURL}/submissions/bundle`;

export const contestSubmissionBundleAPI = {
  getSubmissions: (token, contestJid, username, problemAlias, page) => {
    const params = stringify({ contestJid, username, problemAlias, page });
    return get(`${baseURL}?${params}`, token);
  },

  createItemSubmission: (token, data) => {
    return post(baseURL, token, data);
  },

  getSubmissionSummary: (token, contestJid, username, language) => {
    const params = stringify({ contestJid, username, language });
    return get(`${baseURL}/summary?${params}`, token);
  },

  getLatestSubmissions: (token, contestJid, problemAlias, username) => {
    const params = stringify({ contestJid, username, problemAlias });
    return get(`${baseURL}/answers?${params}`, token);
  },

  regradeSubmission: (token, submissionJid) => {
    return post(`${baseURL}/${submissionJid}/regrade`, token);
  },

  regradeSubmissions: (token, contestJid, username, problemJid, problemAlias) => {
    const params = stringify({ contestJid, username, problemJid, problemAlias });
    return post(`${baseURL}/regrade?${params}`, token);
  },
};
