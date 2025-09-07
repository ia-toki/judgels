import { stringify } from 'query-string';

import { download, get, post, postMultipart } from '../http';
import { baseContestsURL } from './contest';

const baseURL = `${baseContestsURL}/submissions/programming`;

export const contestSubmissionProgrammingAPI = {
  getSubmissions: (token, contestJid, username, problemAlias, page) => {
    const params = stringify({ contestJid, username, problemAlias, page });
    return get(`${baseURL}?${params}`, token);
  },

  getUserProblemSubmissions: (token, contestJid, userJid, problemJid) => {
    const params = stringify({ contestJid, userJid, problemJid });
    return get(`${baseURL}/user-problem?${params}`, token);
  },

  getSubmissionWithSource: (token, submissionId, language) => {
    const params = stringify({ language });
    return get(`${baseURL}/id/${submissionId}?${params}`, token);
  },

  createSubmission: (token, contestJid, problemJid, gradingLanguage, sourceFiles) => {
    const parts = { contestJid, problemJid, gradingLanguage, ...sourceFiles };
    return postMultipart(baseURL, token, parts);
  },

  regradeSubmission: (token, submissionJid) => {
    return post(`${baseURL}/${submissionJid}/regrade`, token);
  },

  regradeSubmissions: (token, contestJid, username, problemAlias) => {
    const params = stringify({ contestJid, username, problemAlias });
    return post(`${baseURL}/regrade?${params}`, token);
  },

  downloadSubmission: (token, submissionJid) => {
    return download(`${baseURL}/${submissionJid}/download`, token);
  },

  getSubmissionSourceImage: (contestJid, userJid, problemJid, isDarkMode) => {
    const params = stringify({ contestJid, userJid, problemJid });
    return Promise.resolve(`${baseURL}/image${isDarkMode ? '/dark' : ''}?${params}`);
  },

  getSubmissionInfo: (contestJid, userJid, problemJid) => {
    const params = stringify({ contestJid, userJid, problemJid });
    return get(`${baseURL}/info?${params}`);
  },
};
