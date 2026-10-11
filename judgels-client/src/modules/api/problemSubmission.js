import { stringify } from 'query-string';

import { get, post, postMultipart } from './http';
import { baseProblemURL } from './problem';

const baseURL = problemJid => `${baseProblemURL(problemJid)}/submissions/programming`;

export const problemSubmissionAPI = {
  getSubmissions: (token, problemJid, page) => {
    const params = stringify({ page });
    return get(`${baseURL(problemJid)}?${params}`, token);
  },

  getSubmissionConfig: (token, problemJid) => {
    return get(`${baseURL(problemJid)}/config`, token);
  },

  getSubmissionWithSourceById: (token, problemJid, submissionId) => {
    return get(`${baseURL(problemJid)}/id/${submissionId}`, token);
  },

  createSubmission: (token, problemJid, gradingLanguage, sourceFiles) => {
    const parts = { gradingLanguage, ...sourceFiles };
    return postMultipart(baseURL(problemJid), token, parts);
  },

  regradeSubmission: (token, problemJid, submissionJid) => {
    return post(`${baseURL(problemJid)}/${submissionJid}/regrade`, token);
  },

  regradeSubmissions: (token, problemJid) => {
    return post(`${baseURL(problemJid)}/regrade`, token);
  },
};
