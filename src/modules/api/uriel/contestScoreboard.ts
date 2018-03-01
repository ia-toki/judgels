import { APP_CONFIG } from '../../../conf';
import { get } from '../http';
import { Scoreboard } from './scoreboard';

export interface ContestScoreboard {
  type: ContestScoreboardType;
  scoreboard: Scoreboard;
  contestantDisplayNames: { [contestantJid: string]: string };
}

enum ContestScoreboardType {
  Frozen = 'FROZEN',
  Official = 'OFFICIAL',
}

export function createContestScoreboardAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getScoreboard: (contestJid: string): Promise<ContestScoreboard> => {
      return get(`${baseURL}/${contestJid}/scoreboard`);
    },
  };
}
