import { stringify } from 'query-string';

import { get, post, put } from 'modules/api/http';
import { ProfilesMap } from 'modules/api/jophiel/profile';

import { Page } from '../pagination';
import { baseContestURL } from './contest';

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

export interface ContestClarificationAnswer {
  answer: string;
}

export interface ContestClarificationData {
  topicJid: string;
  title: string;
  question: string;
}

export interface ContestClarificationConfig {
  canCreate: boolean;
  canSupervise: boolean;
  canManage: boolean;
  problemJids: string[];
}

export interface ContestClarificationsResponse {
  data: Page<ContestClarification>;
  config: ContestClarificationConfig;
  profilesMap: ProfilesMap;
  problemAliasesMap: { [problemJid: string]: string };
  problemNamesMap: { [problemJid: string]: string };
}

const baseURL = (contestJid: string) => `${baseContestURL(contestJid)}/clarifications`;

export const contestClarificationAPI = {
  createClarification: (token: string, contestJid: string, data: ContestClarificationData): Promise<void> => {
    return post(`${baseURL(contestJid)}`, token, data);
  },

  getClarifications: (
    token: string,
    contestJid: string,
    language?: string,
    page?: number
  ): Promise<ContestClarificationsResponse> => {
    const params = stringify({ language, page });
    return get(`${baseURL(contestJid)}?${params}`, token);
  },

  answerClarification: (
    token: string,
    contestJid: string,
    clarificationJid: string,
    data: ContestClarificationAnswer
  ): Promise<void> => {
    return put(`${baseURL(contestJid)}/${clarificationJid}/answers`, token, data);
  },
};
