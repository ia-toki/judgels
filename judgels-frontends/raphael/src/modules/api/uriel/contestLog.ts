import { stringify } from 'query-string';

import { get } from '../http';
import { Page } from '../pagination';
import { ProfilesMap } from '../jophiel/profile';
import { baseContestURL } from './contest';

export interface ContestLog {
  userJid: string;
  event: string;
  object?: string;
  problemJid?: string;
  ipAddress?: string;
  time?: number;
}

export interface ContestLogConfig {
  userJids: string[];
  problemJids: string[];
}

export interface ContestLogsResponse {
  data: Page<ContestLog>;
  config: ContestLogConfig;
  profilesMap: ProfilesMap;
  problemAliasesMap: { [problemJid: string]: string };
}

const baseURL = (contestJid: string) => `${baseContestURL(contestJid)}/logs`;

export const contestLogAPI = {
  getLogs: (
    token: string,
    contestJid: string,
    userJid?: string,
    problemJid?: string,
    page?: number
  ): Promise<ContestLogsResponse> => {
    const params = stringify({ userJid, problemJid, page });
    return get(`${baseURL(contestJid)}?${params}`, token);
  },
};
