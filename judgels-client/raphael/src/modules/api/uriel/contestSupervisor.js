import { stringify } from 'query-string';

import { get, post } from '../http';
import { baseContestURL } from './contest';

export const SupervisorManagementPermission = {
  All: 'ALL',
  Announcements: 'ANNOUNCEMENT',
  Problems: 'PROBLEM',
  Submissions: 'SUBMISSION',
  Clarifications: 'CLARIFICATION',
  Teams: 'TEAM',
  Scoreboard: 'SCOREBOARD',
  Files: 'FILE',
};

export const supervisorManagementPermissionShortNamesMap = {
  [SupervisorManagementPermission.All]: 'ALL',
  [SupervisorManagementPermission.Announcements]: 'ANNC',
  [SupervisorManagementPermission.Problems]: 'PROB',
  [SupervisorManagementPermission.Submissions]: 'SUBM',
  [SupervisorManagementPermission.Clarifications]: 'CLAR',
  [SupervisorManagementPermission.Teams]: 'TEAM',
  [SupervisorManagementPermission.Scoreboard]: 'SCOR',
  [SupervisorManagementPermission.Files]: 'FILE',
};

export const supervisorManagementPermissions = Object.keys(SupervisorManagementPermission);

const baseURL = contestJid => `${baseContestURL(contestJid)}/supervisors`;

export const contestSupervisorAPI = {
  getSupervisors: (token, contestJid, page) => {
    const params = stringify({ page });
    return get(`${baseURL(contestJid)}?${params}`, token);
  },

  upsertSupervisors: (token, contestJid, data) => {
    return post(`${baseURL(contestJid)}/batch-upsert`, token, data);
  },

  deleteSupervisors: (token, contestJid, usernames) => {
    return post(`${baseURL(contestJid)}/batch-delete`, token, usernames);
  },
};
