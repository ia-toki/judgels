import { stringify } from 'query-string';

import { get, post } from '../../../modules/api/http';
import { Page } from '../../../modules/api/pagination';
import { ProfilesMap } from '../../../modules/api/jophiel/profile';

import { baseContestURL } from './contest';

export enum SupervisorManagementPermission {
  All = 'ALL',
  Announcements = 'ANNOUNCEMENT',
  Problems = 'PROBLEM',
  Submissions = 'SUBMISSION',
  Clarifications = 'CLARIFICATION',
  Teams = 'TEAM',
  Scoreboard = 'SCOREBOARD',
  Files = 'FILE',
}

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

export interface ContestSupervisor {
  userJid: string;
  managementPermissions: SupervisorManagementPermission[];
}

export interface ContestSupervisorsResponse {
  data: Page<ContestSupervisor>;
  profilesMap: ProfilesMap;
}

export interface ContestSupervisorsUpsertResponse {
  upsertedSupervisorProfilesMap: ProfilesMap;
}

export interface ContestSupervisorsDeleteResponse {
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
  ): Promise<ContestSupervisorsUpsertResponse> => {
    return post(`${baseURL(contestJid)}/batch-upsert`, token, data);
  },

  deleteSupervisors: (
    token: string,
    contestJid: string,
    usernames: string[]
  ): Promise<ContestSupervisorsDeleteResponse> => {
    return post(`${baseURL(contestJid)}/batch-delete`, token, usernames);
  },
};
