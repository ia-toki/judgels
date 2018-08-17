import { stringify } from 'query-string';

import { APP_CONFIG } from 'conf';
import { get, post } from 'modules/api/http';
import { Page } from 'modules/api/pagination';

export interface Contest {
  id: number;
  jid: string;
  name: string;
  slug: string;
  description: string;
  style: ContestStyle;
  beginTime: number;
  duration: number;
}

export interface ContestDescription {
  jid: string;
  description: string;
}

export enum ContestStyle {
  ICPC = 'ICPC',
  IOI = 'IOI',
}

export interface ContestPage extends Page<Contest> {}

export function createContestAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getContests: (token: string, page: number, pageSize: number): Promise<ContestPage> => {
      const params = stringify({ page, pageSize });
      return get(`${baseURL}?${params}`, token);
    },

    getActiveContests: (token: string): Promise<Contest[]> => {
      return get(`${baseURL}/active`, token);
    },

    getPastContests: (token: string, page: number, pageSize: number): Promise<ContestPage> => {
      const params = stringify({ page, pageSize });
      return get(`${baseURL}/past?${params}`, token);
    },

    getContestBySlug: (token: string, contestSlug: string): Promise<Contest> => {
      return get(`${baseURL}/slug/${contestSlug}`, token);
    },

    startVirtualContest: (token: string, contestJid: string): Promise<Contest> => {
      return post(`${baseURL}/${contestJid}/virtual`, token);
    },

    getContestDescription: (token: string, contestJid: string): Promise<ContestDescription> => {
      return get(`${baseURL}/${contestJid}`, token); 
    },
  };
}
