import { APP_CONFIG } from '../../../conf';
import { get } from '../http';
import { UsersMap } from '../jophiel/user';
import { Scoreboard } from './scoreboard';

export interface ContestScoreboard {
  type: ContestScoreboardType;
  scoreboard: Scoreboard;
  updatedTime: number;
}

enum ContestScoreboardType {
  Frozen = 'FROZEN',
  Official = 'OFFICIAL',
}

export interface ContestScoreboardResponse {
  data: ContestScoreboard;
  usersMap: UsersMap;
}

export function createContestScoreboardAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getScoreboard: (token: string, contestJid: string): Promise<ContestScoreboardResponse> => {
      return get(`${baseURL}/${contestJid}/scoreboard`, token);
    },
  };
}
