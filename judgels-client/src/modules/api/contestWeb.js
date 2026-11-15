import { APP_CONFIG } from '../../conf';
import { get } from './http';

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

const baseURL = `${APP_CONFIG.apiUrl}/v2/contest-web`;

export const contestWebAPI = {
  getWebConfig: (token, contestJid) => {
    return get(`${baseURL}/${contestJid}/config`, token);
  },
};

export const REFRESH_WEB_CONFIG_INTERVAL = 20000; // 20 seconds
