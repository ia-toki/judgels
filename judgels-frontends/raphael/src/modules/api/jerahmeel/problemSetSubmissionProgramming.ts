import { stringify } from 'query-string';

import { get, postMultipart } from '../http';
import { SubmissionWithSourceResponse } from '../sandalphon/submissionProgramming';
import { SubmissionsResponse } from './submissionProgramming';
import { baseProblemSetsURL } from './problemSet';

const baseURL = `${baseProblemSetsURL}/submissions/programming`;

export const problemSetSubmissionProgrammingAPI = {
  getSubmissions: (
    token: string,
    problemSetJid?: string,
    userJid?: string,
    problemJid?: string,
    page?: number
  ): Promise<SubmissionsResponse> => {
    const params = stringify({ problemSetJid, userJid, problemJid, page });
    return get(`${baseURL}?${params}`, token);
  },

  getSubmissionWithSource: (
    token: string,
    submissionId: number,
    language?: string
  ): Promise<SubmissionWithSourceResponse> => {
    const params = stringify({ language });
    return get(`${baseURL}/id/${submissionId}?${params}`, token);
  },

  createSubmission: (
    token: string,
    problemSetJid: string,
    problemJid: string,
    gradingLanguage: string,
    sourceFiles: { [key: string]: File }
  ): Promise<void> => {
    const parts = { problemSetJid, problemJid, gradingLanguage, ...sourceFiles };
    return postMultipart(baseURL, token, parts);
  },
};
