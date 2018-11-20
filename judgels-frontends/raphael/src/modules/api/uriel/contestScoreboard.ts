import { stringify } from 'query-string';

import { APP_CONFIG } from 'conf';
import { get } from 'modules/api/http';
import { ProfilesMap } from 'modules/api/jophiel/profile';

import { Scoreboard } from './scoreboard';

export interface ContestScoreboard {
  type: ContestScoreboardType;
  scoreboard: Scoreboard;
  updatedTime: number;
}

export enum ContestScoreboardType {
  Frozen = 'FROZEN',
  Official = 'OFFICIAL',
}

export interface ContestScoreboardConfig {
  canViewOfficialAndFrozen: boolean;
  canViewClosedProblems: boolean;
}

export interface ContestScoreboardResponse {
  data: ContestScoreboard;
  profilesMap: ProfilesMap;
  config: ContestScoreboardConfig;
}

export function createContestScoreboardAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getScoreboard: (
      token: string,
      contestJid: string,
      frozen?: boolean,
      showClosedProblems?: boolean
    ): Promise<ContestScoreboardResponse | null> => {
      const params = stringify({ frozen, showClosedProblems });
      return get(`${baseURL}/${contestJid}/scoreboard?${params}`, token);
    },
  };
}
