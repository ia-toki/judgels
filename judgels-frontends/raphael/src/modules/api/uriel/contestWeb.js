import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

export const ContestTab = {
  Overview: 'OVERVIEW',
  Announcements: 'ANNOUNCEMENTS',
  Problems: 'PROBLEMS',
  Editorial: 'EDITORIAL',
  Contestants: 'CONTESTANTS',
  Supervisors: 'SUPERVISORS',
  Managers: 'MANAGERS',
  Teams: 'TEAMS',
  Submissions: 'SUBMISSIONS',
  Clarifications: 'CLARIFICATIONS',
  Scoreboard: 'SCOREBOARD',
  Files: 'FILES',
  Logs: 'LOGS',
};

export const ContestRole = {
  Admin: 'ADMIN',
  Manager: 'MANAGER',
  Supervisor: 'SUPERVISOR',
  Contestant: 'CONTESTANT',
  None: 'NONE',
};

export const ContestState = {
  NotBegun: 'NOT_BEGUN',
  Begun: 'BEGUN',
  Started: 'STARTED',
  Finished: 'FINISHED',
  Paused: 'PAUSED',
};

const baseURL = `${APP_CONFIG.apiUrls.uriel}/contest-web`;

export const contestWebAPI = {
  getContestBySlugWithWebConfig: (token, contestSlug) => {
    return get(`${baseURL}/slug/${contestSlug}/with-config`, token);
  },

  getContestByJidWithWebConfig: (token, contestJid) => {
    return get(`${baseURL}/${contestJid}/with-config`, token);
  },

  getWebConfig: (token, contestJid) => {
    return get(`${baseURL}/${contestJid}/config`, token);
  },
};

export const REFRESH_WEB_CONFIG_INTERVAL = 20000; // 20 seconds
