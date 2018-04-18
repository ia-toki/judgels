import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { Page } from '../pagination';
import { get } from '../http';

export interface Contest {
  id: number;
  jid: string;
  name: string;
  description: string;
  style: ContestStyle;
  beginTime: number;
  duration: number;
}

export enum ContestStyle {
  ICPC = 'ICPC',
  IOI = 'IOI',
}

export interface ContestPage extends Page<Contest> {}

export function createContestAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getActiveContests: (token: string): Promise<Contest[]> => {
      return get(`${baseURL}/active`, token);
    },

    getPastContests: (token: string, page: number, pageSize: number): Promise<ContestPage> => {
      const params = stringify({ page, pageSize });
      return get(`${baseURL}/past?${params}`, token);
    },

    getContest: (token: string, contestJid: string): Promise<Contest> => {
      return get(`${baseURL}/${contestJid}`, token);
    },
  };
}
