import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

export enum ContestTab {
  Announcements = 'ANNOUNCEMENTS',
  Problems = 'PROBLEMS',
  Submissions = 'SUBMISSIONS',
  Clarifications = 'CLARIFICATIONS',
  Scoreboard = 'SCOREBOARD',
}

export interface ContestWebConfig {
  visibleTabs: ContestTab[];
  contestState: ContestState;
  remainingContestStateDuration?: number;
  announcementsCount: number;
  answeredClarificationsCount: number;
}

export enum ContestState {
  None = 'NONE',
  NotBegun = 'NOT_BEGUN',
  Running = 'RUNNING',
  Ended = 'ENDED',
}

export function createContestWebAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getConfig: (token: string, contestJid: string): Promise<ContestWebConfig> => {
      return get(`${baseURL}/${contestJid}/web/config`, token);
    },
  };
}
