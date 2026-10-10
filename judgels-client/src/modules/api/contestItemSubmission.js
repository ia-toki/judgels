import { stringify } from 'query-string';

import { baseContestsURL } from './contest';
import { get, post } from './http';

const baseURL = `${baseContestsURL}/submissions/bundle`;

export const contestItemSubmissionAPI = {
  getItemSubmissions: (token, contestJid, username, problemAlias, page) => {
    const params = stringify({ contestJid, username, problemAlias, page });
    return get(`${baseURL}?${params}`, token);
  },

  createItemSubmission: (token, data) => {
    return post(baseURL, token, data);
  },

  getItemSubmissionSummary: (token, contestJid, username, language) => {
    const params = stringify({ contestJid, username, language });
    return get(`${baseURL}/summary?${params}`, token);
  },

  getLatestItemSubmissions: (token, contestJid, problemAlias, username) => {
    const params = stringify({ contestJid, username, problemAlias });
    return get(`${baseURL}/answers?${params}`, token);
  },

  regradeItemSubmission: (token, submissionJid) => {
    return post(`${baseURL}/${submissionJid}/regrade`, token);
  },

  regradeItemSubmissions: (token, contestJid, username, problemJid, problemAlias) => {
    const params = stringify({ contestJid, username, problemJid, problemAlias });
    return post(`${baseURL}/regrade?${params}`, token);
  },
};
