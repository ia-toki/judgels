import { stringify } from 'query-string';

import { get, post } from 'modules/api/http';
import { Page } from 'modules/api/pagination';
import { ProfilesMap } from 'modules/api/jophiel/profile';

import { baseContestURL } from './contest';

export enum SupervisorManagementPermission {
  All = 'ALL',
  Announcement = 'ANNOUNCEMENT',
  Problem = 'PROBLEM',
  Submission = 'SUBMISSION',
  Clarification = 'CLARIFICATION',
  Team = 'TEAM',
  Scoreboard = 'SCOREBOARD',
  File = 'FILE',
}

export const SupervisorPermissionFullNameMap = Object.keys(SupervisorManagementPermission)
  .filter(p => p !== 'All')
  .reduce((map, p) => {
    map[SupervisorManagementPermission[p]] = p;
    return map;
  }, {});

export const SupervisorPermissionShortNameMap = {
  [SupervisorManagementPermission.All]: 'ALL',
  [SupervisorManagementPermission.Announcement]: 'ANNC',
  [SupervisorManagementPermission.Problem]: 'PRBM',
  [SupervisorManagementPermission.Submission]: 'SUBM',
  [SupervisorManagementPermission.Clarification]: 'CLRF',
  [SupervisorManagementPermission.Team]: 'TEAM',
  [SupervisorManagementPermission.Scoreboard]: 'SCRB',
  [SupervisorManagementPermission.File]: 'FILE',
};

export interface ContestSupervisor {
  userJid: string;
  managementPermissions: SupervisorManagementPermission[];
}

export interface ContestSupervisorsResponse {
  data: Page<ContestSupervisor>;
  profilesMap: ProfilesMap;
}

export interface ContestSupervisorUpsertResponse {
  upsertedSupervisorProfilesMap: ProfilesMap;
}

export interface ContestSupervisorDeleteResponse {
  deletedSupervisorProfilesMap: ProfilesMap;
}

export interface ContestSupervisorUpsertData {
  usernames: string[];
  managementPermissions: SupervisorManagementPermission[];
}

const baseURL = (contestJid: string) => `${baseContestURL(contestJid)}/supervisors`;

export const contestSupervisorAPI = {
  getSupervisors: (token: string, contestJid: string, page?: number): Promise<ContestSupervisorsResponse> => {
    const params = stringify({ page });
    return get(`${baseURL(contestJid)}?${params}`, token);
  },

  upsertSupervisors: (
    token: string,
    contestJid: string,
    data: ContestSupervisorUpsertData
  ): Promise<ContestSupervisorUpsertResponse> => {
    return post(`${baseURL(contestJid)}/batch-upsert`, token, data);
  },

  deleteSupervisors: (
    token: string,
    contestJid: string,
    usernames: string[]
  ): Promise<ContestSupervisorDeleteResponse> => {
    return post(`${baseURL(contestJid)}/batch-delete`, token, usernames);
  },
};
