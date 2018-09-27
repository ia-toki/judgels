import { stringify } from 'query-string';

import { APP_CONFIG } from 'conf';
import { get, post } from 'modules/api/http';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { Page } from '../pagination';

export enum ContestClarificationStatus {
  Asked = 'ASKED',
  Answered = 'ANSWERED',
}

export interface ContestClarification {
  id: number;
  jid: string;
  userJid: string;
  topicJid: string;
  title: string;
  question: string;
  status: ContestClarificationStatus;
  time: number;

  answererJid?: string;
  answer?: string;
  answeredTime?: number;
}

export interface ContestClarificationData {
  topicJid: string;
  title: string;
  question: string;
}

export interface ContestClarificationConfig {
  isAllowedToCreateClarification: boolean;
  problemJids: string[];
  problemAliasesMap: { [problemJid: string]: string };
  problemNamesMap: { [problemJid: string]: string };
}

export interface ContestClarificationsResponse {
  data: Page<ContestClarification>;
  profilesMap: ProfilesMap;
  problemAliasesMap: { [problemJid: string]: string };
  problemNamesMap: { [problemJid: string]: string };
}

export function createContestClarificationAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    createClarification: (token: string, contestJid: string, data: ContestClarificationData): Promise<void> => {
      return post(`${baseURL}/${contestJid}/clarifications`, token, data);
    },

    getClarificationConfig: (
      token: string,
      contestJid: string,
      language: string
    ): Promise<ContestClarificationConfig> => {
      const languageParam = language ? `?language=${language}` : '';
      return get(`${baseURL}/${contestJid}/clarifications/config${languageParam}`, token);
    },

    getClarifications: (
      token: string,
      contestJid: string,
      language: string,
      page: number
    ): Promise<ContestClarificationsResponse> => {
      const params = stringify({ language, page });
      return get(`${baseURL}/${contestJid}/clarifications?${params}`, token);
    },
  };
}
