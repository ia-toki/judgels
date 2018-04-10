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

export interface ContestList extends Page<Contest> {}

export function createContestAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getContests: (token: string, page: number, pageSize: number): Promise<ContestList> => {
      return get(`${baseURL}?page=${page}&pageSize=${pageSize}`, token);
    },

    getContest: (token: string, contestJid: string): Promise<Contest> => {
      return get(`${baseURL}/${contestJid}`, token);
    },
  };
}
