import { APP_CONFIG } from '../../../conf';
import { Page } from '../pagination';
import { get } from '../http';

export interface Contest {
  id: number;
  name: string;
}

export interface ContestScoreboard {}

export interface ContestList extends Page<Contest> {}

export function createContestAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getContests: (page: number, pageSize: number): Promise<ContestList> => {
      return get(`${baseURL}?page=${page}&pageSize=${pageSize}`);
    },

    getContest: (contestJid: string): Promise<Contest> => {
      return get(`${baseURL}/${contestJid}`);
    },
  };
}
