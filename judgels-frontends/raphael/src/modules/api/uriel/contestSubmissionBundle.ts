import { stringify } from 'query-string';
import { get, post } from 'modules/api/http';

import { Page } from 'modules/api/pagination';
import { ItemSubmission } from 'modules/api/sandalphon/submissionBundle';
import { ContestSubmissionConfig } from './contestSubmission';
import { Profile } from 'modules/api/jophiel/profile';
import { baseContestsURL } from './contest';
import { ItemType } from '../sandalphon/problemBundle';

export interface ContestItemSubmissionData {
  contestJid: string;
  problemJid: string;
  itemJid: string;
  answer: string;
}

export interface ContestItemSubmissionsResponse {
  data: Page<ItemSubmission>;
  config: ContestSubmissionConfig;
  profilesMap: { [id: string]: Profile };
  problemAliasesMap: { [id: string]: string };
  itemNumbersMap: { [itemJid: string]: number };
  itemTypesMap: { [itemJid: string]: ItemType };
}

export interface ContestantAnswerSummaryResponse {
  profile: Profile;
  config: ContestSubmissionConfig;
  itemJidsByProblemJid: { [problemJid: string]: string[] };
  submissionsByItemJid: { [itemJid: string]: ItemSubmission };
  problemAliasesMap: { [id: string]: string };
  problemNamesMap: { [id: string]: string };
  itemTypesMap: { [itemJid: string]: ItemType };
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

  createItemSubmission: (token: string, data: ContestItemSubmissionData): Promise<void> => {
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
