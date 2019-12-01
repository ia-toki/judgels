import { stringify } from 'query-string';

import { get, post } from '../http';
import { ItemSubmission } from '../sandalphon/submissionBundle';
import { AnswerSummaryResponse, ItemSubmissionData, ItemSubmissionsResponse } from './submissionBundle';
import { baseProblemSetsURL } from './problemSet';

const baseURL = `${baseProblemSetsURL}/submissions/bundle`;

export const problemSetSubmissionBundleAPI = {
  getSubmissions: (
    token: string,
    problemSetJid: string,
    username?: string,
    problemAlias?: string,
    page?: number
  ): Promise<ItemSubmissionsResponse> => {
    const params = stringify({ problemSetJid, username, problemAlias, page });
    return get(`${baseURL}?${params}`, token);
  },

  createItemSubmission: (token: string, data: ItemSubmissionData): Promise<void> => {
    return post(`${baseURL}/`, token, data);
  },

  getAnswerSummary: (
    token: string,
    problemSetJid: string,
    username?: string,
    language?: string
  ): Promise<AnswerSummaryResponse> => {
    const params = stringify({ problemSetJid, username, language });
    return get(`${baseURL}/summary?${params}`, token);
  },

  getLatestSubmissionsByUserForProblemInProblemSet: (
    token: string,
    problemSetJid: string,
    problemAlias: string,
    username?: string
  ): Promise<{ [id: string]: ItemSubmission }> => {
    const params = stringify({ problemSetJid, username, problemAlias });
    return get(`${baseURL}/answers?${params}`, token);
  },

  regradeSubmission: (token: string, submissionJid: string): Promise<void> => {
    return post(`${baseURL}/${submissionJid}/regrade`, token);
  },

  regradeSubmissions: (token: string, problemSetJid?: string, userJid?: string, problemJid?: string): Promise<void> => {
    const params = stringify({ problemSetJid, userJid, problemJid });
    return post(`${baseURL}/regrade?${params}`, token);
  },
};
