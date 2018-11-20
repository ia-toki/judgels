import { APP_CONFIG } from 'conf';
import { get } from 'modules/api/http';

import { Contest } from './contest';
import { ContestClarificationStatus } from './contestClarification';

export enum ContestTab {
  Announcements = 'ANNOUNCEMENTS',
  Problems = 'PROBLEMS',
  Contestants = 'CONTESTANTS',
  Managers = 'MANAGERS',
  Submissions = 'SUBMISSIONS',
  Clarifications = 'CLARIFICATIONS',
  Scoreboard = 'SCOREBOARD',
}

export interface ContestWithWebConfig {
  contest: Contest;
  config: ContestWebConfig;
}

export interface ContestWebConfig {
  canManage: boolean;
  visibleTabs: ContestTab[];
  state: ContestState;
  remainingStateDuration?: number;
  announcementCount: number;
  clarificationCount: number;
  clarificationStatus: ContestClarificationStatus;
}

export enum ContestState {
  NotBegun = 'NOT_BEGUN',
  Begun = 'BEGUN',
  Started = 'STARTED',
  Finished = 'FINISHED',
  Paused = 'PAUSED',
}

export function createContestWebAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contest-web`;

  return {
    getContestBySlugWithWebConfig: (token: string, contestSlug: string): Promise<ContestWithWebConfig> => {
      return get(`${baseURL}/slug/${contestSlug}/with-config`, token);
    },

    getContestByJidWithWebConfig: (token: string, contestJid: string): Promise<ContestWithWebConfig> => {
      return get(`${baseURL}/${contestJid}/with-config`, token);
    },

    getWebConfig: (token: string, contestJid: string): Promise<ContestWebConfig> => {
      return get(`${baseURL}/${contestJid}/config`, token);
    },
  };
}
