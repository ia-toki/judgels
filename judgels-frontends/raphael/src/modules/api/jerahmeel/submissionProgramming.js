import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get, postMultipart, post } from '../http';

export const baseSubmissionsURL = `${APP_CONFIG.apiUrls.jerahmeel}/submissions/programming`;

export const submissionProgrammingAPI = {
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

  getSubmissionSourceImage: submissionJid => Promise.resolve(`${baseSubmissionsURL}/${submissionJid}/image`),
};
