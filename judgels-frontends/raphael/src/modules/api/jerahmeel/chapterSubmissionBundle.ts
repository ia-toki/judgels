import { stringify } from 'query-string';

import { get, post } from '../../../modules/api/http';
import { ItemSubmission } from '../../../modules/api/sandalphon/submissionBundle';
import { AnswerSummaryResponse, ItemSubmissionData, ItemSubmissionsResponse } from './submissionBundle';
import { baseChaptersURL } from './chapter';

const baseURL = `${baseChaptersURL}/submissions/bundle`;

export const chapterSubmissionBundleAPI = {
  getSubmissions: (
    token: string,
    chapterJid: string,
    username?: string,
    problemAlias?: string,
    page?: number
  ): Promise<ItemSubmissionsResponse> => {
    const params = stringify({ chapterJid, username, problemAlias, page });
    return get(`${baseURL}?${params}`, token);
  },

  createItemSubmission: (token: string, data: ItemSubmissionData): Promise<void> => {
    return post(`${baseURL}/`, token, data);
  },

  getAnswerSummary: (
    token: string,
    chapterJid: string,
    username?: string,
    language?: string
  ): Promise<AnswerSummaryResponse> => {
    const params = stringify({ chapterJid, username, language });
    return get(`${baseURL}/summary?${params}`, token);
  },

  getLatestSubmissionsByUserForProblemInChapter: (
    token: string,
    chapterJid: string,
    problemAlias: string,
    username?: string
  ): Promise<{ [id: string]: ItemSubmission }> => {
    const params = stringify({ chapterJid, username, problemAlias });
    return get(`${baseURL}/answers?${params}`, token);
  },

  regradeSubmission: (token: string, submissionJid: string): Promise<void> => {
    return post(`${baseURL}/${submissionJid}/regrade`, token);
  },

  regradeSubmissions: (token: string, chapterJid?: string, userJid?: string, problemJid?: string): Promise<void> => {
    const params = stringify({ chapterJid, userJid, problemJid });
    return post(`${baseURL}/regrade?${params}`, token);
  },
};
