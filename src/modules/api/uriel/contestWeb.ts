import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

export enum ContestTab {
  Announcements = 'ANNOUNCEMENTS',
  Problems = 'PROBLEMS',
  Scoreboard = 'SCOREBOARD',
  Submissions = 'SUBMISSIONS',
}

export interface ContestWebConfig {
  visibleTabs: ContestTab[];
}

export function createContestWebAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getConfig: (token: string, contestJid: string): Promise<ContestWebConfig> => {
      return get(`${baseURL}/${contestJid}/web/config`, token);
    },
  };
}
