import { stringify } from 'query-string';

import { get, post } from './http';
import { baseProblemURL } from './problem';

const baseURL = problemJid => `${baseProblemURL(problemJid)}/submissions/bundle`;

export const problemBundleSubmissionAPI = {
  getBundleSubmissions: (token, problemJid, page) => {
    const params = stringify({ page });
    return get(`${baseURL(problemJid)}?${params}`, token);
  },

  getBundleSubmissionWithAnswerById: (token, problemJid, submissionId) => {
    return get(`${baseURL(problemJid)}/id/${submissionId}`, token);
  },

  createBundleSubmission: (token, problemJid, data) => {
    return post(baseURL(problemJid), token, data);
  },

  regradeBundleSubmission: (token, problemJid, submissionJid) => {
    return post(`${baseURL(problemJid)}/${submissionJid}/regrade`, token);
  },

  regradeBundleSubmissions: (token, problemJid) => {
    return post(`${baseURL(problemJid)}/regrade`, token);
  },
};
