import { stringify } from 'query-string';

import { get, post } from '../http';
import { Page } from '../pagination';
import { ItemSubmission } from '../sandalphon/submissionBundle';
import { ProblemSetSubmissionConfig } from './problemSetSubmission';
import { Profile } from '../jophiel/profile';
import { baseProblemSetsURL } from './problemSet';
import { ItemType } from '../sandalphon/problemBundle';

export interface ProblemSetItemSubmissionData {
  problemSetJid: string;
  problemJid: string;
  itemJid: string;
  answer: string;
}

export interface ProblemSetItemSubmissionsResponse {
  data: Page<ItemSubmission>;
  config: ProblemSetSubmissionConfig;
  profilesMap: { [id: string]: Profile };
  problemAliasesMap: { [id: string]: string };
  itemNumbersMap: { [itemJid: string]: number };
  itemTypesMap: { [itemJid: string]: ItemType };
}

export interface AnswerSummaryResponse {
  profile: Profile;
  config: ProblemSetSubmissionConfig;
  itemJidsByProblemJid: { [problemJid: string]: string[] };
  submissionsByItemJid: { [itemJid: string]: ItemSubmission };
  problemAliasesMap: { [id: string]: string };
  problemNamesMap: { [id: string]: string };
  itemTypesMap: { [itemJid: string]: ItemType };
}

const baseURL = `${baseProblemSetsURL}/submissions/bundle`;

export const problemSetSubmissionBundleAPI = {
  getSubmissions: (
    token: string,
    problemSetJid: string,
    username?: string,
    problemAlias?: string,
    page?: number
  ): Promise<ProblemSetItemSubmissionsResponse> => {
    const params = stringify({ problemSetJid, username, problemAlias, page });
    return get(`${baseURL}?${params}`, token);
  },

  createItemSubmission: (token: string, data: ProblemSetItemSubmissionData): Promise<void> => {
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
