import { stringify } from 'query-string';

import { get, post } from '../http';
import {
  AnswerSummaryResponse,
  ItemSubmission,
  ItemSubmissionData,
  ItemSubmissionsResponse,
} from '../sandalphon/submissionBundle';
import { ContestSubmissionConfig } from './contestSubmission';
import { baseContestsURL } from './contest';

export interface ContestItemSubmissionsResponse extends ItemSubmissionsResponse {
  config: ContestSubmissionConfig;
}

export interface ContestantAnswerSummaryResponse extends AnswerSummaryResponse {
  config: ContestSubmissionConfig;
}

const baseURL = `${baseContestsURL}/submissions/bundle`;

export const contestSubmissionBundleAPI = {
  getSubmissions: (
    token: string,
    contestJid: string,
    username?: string,
    problemAlias?: string,
    page?: number
  ): Promise<ContestItemSubmissionsResponse> => {
    const params = stringify({ contestJid, username, problemAlias, page });
    return get(`${baseURL}?${params}`, token);
  },

  createItemSubmission: (token: string, data: ItemSubmissionData): Promise<void> => {
    return post(`${baseURL}/`, token, data);
  },

  getAnswerSummaryForContestant: (
    token: string,
    contestJid: string,
    username?: string,
    language?: string
  ): Promise<ContestantAnswerSummaryResponse> => {
    const params = stringify({ contestJid, username, language });
    return get(`${baseURL}/summary?${params}`, token);
  },

  getLatestSubmissionsByUserForProblemInContest: (
    token: string,
    contestJid: string,
    problemAlias: string,
    username?: string
  ): Promise<{ [id: string]: ItemSubmission }> => {
    const params = stringify({ contestJid, username, problemAlias });
    return get(`${baseURL}/answers?${params}`, token);
  },

  regradeSubmission: (token: string, submissionJid: string): Promise<void> => {
    return post(`${baseURL}/${submissionJid}/regrade`, token);
  },

  regradeSubmissions: (token: string, contestJid?: string, userJid?: string, problemJid?: string): Promise<void> => {
    const params = stringify({ contestJid, userJid, problemJid });
    return post(`${baseURL}/regrade?${params}`, token);
  },
};
