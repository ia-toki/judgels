import { stringify } from 'query-string';

import { get, post } from '../../../modules/api/http';
import { ProfilesMap } from '../../../modules/api/jophiel/profile';

import { Scoreboard } from './scoreboard';
import { baseContestURL } from './contest';

export interface ContestScoreboard {
  type: ContestScoreboardType;
  scoreboard: Scoreboard;
  totalEntries: number;
  updatedTime: number;
}

export enum ContestScoreboardType {
  Frozen = 'FROZEN',
  Official = 'OFFICIAL',
}

export interface ContestScoreboardConfig {
  canViewOfficialAndFrozen: boolean;
  canViewClosedProblems: boolean;
  canRefresh: boolean;
  pageSize: number;
}

export interface ContestScoreboardResponse {
  data: ContestScoreboard;
  profilesMap: ProfilesMap;
  config: ContestScoreboardConfig;
}

const baseURL = (contestJid: string) => `${baseContestURL(contestJid)}/scoreboard`;

export const contestScoreboardAPI = {
  getScoreboard: (
    token: string,
    contestJid: string,
    frozen?: boolean,
    showClosedProblems?: boolean,
    page?: number
  ): Promise<ContestScoreboardResponse | null> => {
    const params = stringify({ frozen, showClosedProblems, page });
    return get(`${baseURL(contestJid)}?${params}`, token);
  },

  refreshScoreboard: (token: string, contestJid: string): Promise<void> => {
    return post(`${baseURL(contestJid)}/refresh`, token);
  },
};
