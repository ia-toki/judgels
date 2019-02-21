import { stringify } from 'query-string';
import { get, post } from 'modules/api/http';

import { Page } from 'modules/api/pagination';
import { ItemSubmission } from 'modules/api/sandalphon/submissionBundle';
import { ContestSubmissionConfig } from './contestSubmission';
import { Profile } from 'modules/api/jophiel/profile';
import { baseContestsURL } from './contest';

export interface ContestItemSubmissionData {
  contestJid: string;
  problemJid: string;
  itemJid: string;
  answer?: string;
}

export interface ContestItemSubmissionsResponse {
  data: Page<ItemSubmission>;
  config: ContestSubmissionConfig;
  profilesMap: { [id: string]: Profile };
  problemAliasesMap: { [id: string]: string };
}

const baseURL = `${baseContestsURL}/submissions/bundle`;

export const contestSubmissionBundleAPI = {
  getSubmissions: (
    token: string,
    contestJid: string,
    userJid?: string,
    problemJid?: string,
    page?: number
  ): Promise<ContestItemSubmissionsResponse> => {
    const params = stringify({ contestJid, userJid, problemJid, page });
    return get(`${baseURL}?${params}`, token);
  },

  createItemSubmission: (token: string, data: ContestItemSubmissionData): Promise<void> => {
    return post(`${baseURL}/`, token, data);
  },

  getLatestSubmissionsByUserForProblemInContest: (
    token: string,
    contestJid: string,
    problemJid: string,
    userJid?: string
  ): Promise<{ [id: string]: ItemSubmission }> => {
    const params = stringify({ contestJid, userJid, problemJid });
    return get(`${baseURL}/latest?${params}`, token);
  },
};
