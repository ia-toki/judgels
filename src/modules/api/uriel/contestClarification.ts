import { get, post } from '../http';
import { APP_CONFIG } from '../../../conf';

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

export interface ContestClarificationsResponse {
  data: ContestClarification[];
  problemJids: string[];
  problemAliasesMap: { [problemJid: string]: string };
  problemNamesMap: { [problemJid: string]: string };
}

export function createContestClarificationAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    createClarification: (token: string, contestJid: string, data: ContestClarificationData): Promise<void> => {
      return post(`${baseURL}/${contestJid}/clarifications`, token, data);
    },

    getMyClarifications: (
      token: string,
      contestJid: string,
      language: string
    ): Promise<ContestClarificationsResponse> => {
      const languageParam = language ? `?language=${language}` : '';
      return get(`${baseURL}/${contestJid}/clarifications/mine${languageParam}`, token);
    },
  };
}
