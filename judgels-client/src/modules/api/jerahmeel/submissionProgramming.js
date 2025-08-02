import { stringify } from 'query-string';

import { get, post, postMultipart } from '../http';

export const baseSubmissionsURL = `/api/v2/submissions/programming`;

export const submissionProgrammingAPI = {
  getSubmission: submissionJid => {
    return get(`${baseSubmissionsURL}/${submissionJid}`);
  },

  getSubmissions: (token, containerJid, username, problemJid, problemAlias, page) => {
    const params = stringify({ containerJid, username, problemJid, problemAlias, page });
    return get(`${baseSubmissionsURL}?${params}`, token);
  },

  getSubmissionWithSource: (token, submissionId, language) => {
    const params = stringify({ language });
    return get(`${baseSubmissionsURL}/id/${submissionId}?${params}`, token);
  },

  createSubmission: (token, containerJid, problemJid, gradingLanguage, sourceFiles) => {
    const parts = { containerJid, problemJid, gradingLanguage, ...sourceFiles };
    return postMultipart(baseSubmissionsURL, token, parts);
  },

  regradeSubmission: (token, submissionJid) => {
    return post(`${baseSubmissionsURL}/${submissionJid}/regrade`, token);
  },

  regradeSubmissions: (token, containerJid, username, problemJid, problemAlias) => {
    const params = stringify({ containerJid, username, problemJid, problemAlias });
    return post(`${baseSubmissionsURL}/regrade?${params}`, token);
  },

  getSubmissionSourceImage: (submissionJid, isDarkMode) =>
    Promise.resolve(`${baseSubmissionsURL}/${submissionJid}/image${isDarkMode ? '/dark' : ''}`),
};
