import { stringify } from 'query-string';

import { get, post } from 'modules/api/http';
import { Page } from 'modules/api/pagination';
import { ProfilesMap } from 'modules/api/jophiel/profile';

import { baseContestURL } from './contest';

export enum SupervisorManagementPermission {
  None = 'ALL',
  Announcement = 'ANNOUNCEMENT',
  Problem = 'PROBLEM',
  Submission = 'SUBMISSION',
  Clarification = 'CLARIFICATION',
  Supervisor = 'SUPERVISOR',
  Team = 'TEAM',
  Scoreboard = 'SCOREBOARD',
  File = 'FILE',
}

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
