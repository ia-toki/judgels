import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get, post } from '../http';
import { Page } from '../../../modules/api/pagination';
import { ItemSubmission } from '../../../modules/api/sandalphon/submissionBundle';
import { SubmissionConfig } from './submission';
import { Profile } from '../../../modules/api/jophiel/profile';
import { ItemType } from '../sandalphon/problemBundle';

export const baseSubmissionsURL = `${APP_CONFIG.apiUrls.jerahmeel}/submissions/bundle`;

export interface ItemSubmissionData {
  containerJid: string;
  problemJid: string;
  itemJid: string;
  answer: string;
}

export interface ItemSubmissionsResponse {
  data: Page<ItemSubmission>;
  config: SubmissionConfig;
  profilesMap: { [id: string]: Profile };
  problemAliasesMap: { [id: string]: string };
  itemNumbersMap: { [itemJid: string]: number };
  itemTypesMap: { [itemJid: string]: ItemType };
}

export interface SubmissionSummaryResponse {
  profile: Profile;
  config: SubmissionConfig;
  itemJidsByProblemJid: { [problemJid: string]: string[] };
  submissionsByItemJid: { [itemJid: string]: ItemSubmission };
  problemAliasesMap: { [id: string]: string };
  problemNamesMap: { [id: string]: string };
  itemTypesMap: { [itemJid: string]: ItemType };
}

export const submissionBundleAPI = {
  getSubmissions: (
    token: string,
    containerJid: string,
    username?: string,
    problemAlias?: string,
    page?: number
  ): Promise<ItemSubmissionsResponse> => {
    const params = stringify({ containerJid, username, problemAlias, page });
    return get(`${baseSubmissionsURL}?${params}`, token);
  },

  createItemSubmission: (token: string, data: ItemSubmissionData): Promise<void> => {
    return post(`${baseSubmissionsURL}/`, token, data);
  },

  getSubmissionSummary: (
    token: string,
    containerJid: string,
    problemJid: string,
    username?: string,
    language?: string
  ): Promise<SubmissionSummaryResponse> => {
    const params = stringify({ containerJid, problemJid, username, language });
    return get(`${baseSubmissionsURL}/summary?${params}`, token);
  },

  getLatestSubmissions: (
    token: string,
    containerJid: string,
    problemAlias: string,
    username?: string
  ): Promise<{ [id: string]: ItemSubmission }> => {
    const params = stringify({ containerJid, username, problemAlias });
    return get(`${baseSubmissionsURL}/answers?${params}`, token);
  },

  regradeSubmission: (token: string, submissionJid: string): Promise<void> => {
    return post(`${baseSubmissionsURL}/${submissionJid}/regrade`, token);
  },

  regradeSubmissions: (token: string, containerJid?: string, userJid?: string, problemJid?: string): Promise<void> => {
    const params = stringify({ containerJid, userJid, problemJid });
    return post(`${baseSubmissionsURL}/regrade?${params}`, token);
  },
};
