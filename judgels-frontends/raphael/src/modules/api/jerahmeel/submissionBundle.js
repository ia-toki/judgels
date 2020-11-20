import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get, post } from '../http';

export const baseSubmissionsURL = `${APP_CONFIG.apiUrls.jerahmeel}/submissions/bundle`;

export const submissionBundleAPI = {
  getSubmissions: (token, containerJid, username, problemAlias, page) => {
    const params = stringify({ containerJid, username, problemAlias, page });
    return get(`${baseSubmissionsURL}?${params}`, token);
  },

  createItemSubmission: (token, data) => {
    return post(`${baseSubmissionsURL}/`, token, data);
  },

  getSubmissionSummary: (token, containerJid, problemJid, username, language) => {
    const params = stringify({ containerJid, problemJid, username, language });
    return get(`${baseSubmissionsURL}/summary?${params}`, token);
  },

  getLatestSubmissions: (token, containerJid, problemAlias, username) => {
    const params = stringify({ containerJid, username, problemAlias });
    return get(`${baseSubmissionsURL}/answers?${params}`, token);
  },

  regradeSubmission: (token, submissionJid) => {
    return post(`${baseSubmissionsURL}/${submissionJid}/regrade`, token);
  },

  regradeSubmissions: (token, containerJid, userJid, problemJid) => {
    const params = stringify({ containerJid, userJid, problemJid });
    return post(`${baseSubmissionsURL}/regrade?${params}`, token);
  },
};
