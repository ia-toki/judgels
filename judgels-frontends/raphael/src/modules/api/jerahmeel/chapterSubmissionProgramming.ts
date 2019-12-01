import { stringify } from 'query-string';

import { get, postMultipart } from '../../../modules/api/http';
import { SubmissionWithSourceResponse } from '../../../modules/api/sandalphon/submissionProgramming';
import { SubmissionsResponse } from './submissionProgramming';
import { baseChaptersURL } from './chapter';

const baseURL = `${baseChaptersURL}/submissions/programming`;

export const chapterSubmissionProgrammingAPI = {
  getSubmissions: (
    token: string,
    chapterJid?: string,
    userJid?: string,
    problemJid?: string,
    page?: number
  ): Promise<SubmissionsResponse> => {
    const params = stringify({ chapterJid, userJid, problemJid, page });
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
    chapterJid: string,
    problemJid: string,
    gradingLanguage: string,
    sourceFiles: { [key: string]: File }
  ): Promise<void> => {
    const parts = { chapterJid, problemJid, gradingLanguage, ...sourceFiles };
    return postMultipart(baseURL, token, parts);
  },
};
