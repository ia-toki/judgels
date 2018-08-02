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

export interface ContestScoreboardResponse {
  data: ContestScoreboard;
  profilesMap: ProfilesMap;
}

export function createContestScoreboardAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getScoreboard: (token: string, contestJid: string): Promise<ContestScoreboardResponse | null> => {
      return get(`${baseURL}/${contestJid}/scoreboard`, token);
    },
  };
}
