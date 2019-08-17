import { stringify } from 'query-string';

import { get, post } from 'modules/api/http';
import { Page } from 'modules/api/pagination';
import { ProfilesMap } from 'modules/api/jophiel/profile';

import { baseContestURL } from './contest';

export interface ContestManager {
  userJid: string;
}

export interface ContestManagerConfig {
  canManage: boolean;
}

export interface ContestManagersResponse {
  data: Page<ContestManager>;
  profilesMap: ProfilesMap;
  config: ContestManagerConfig;
}

export interface ContestManagersUpsertResponse {
  insertedManagerProfilesMap: ProfilesMap;
  alreadyManagerProfilesMap: ProfilesMap;
}

export interface ContestManagersDeleteResponse {
  deletedManagerProfilesMap: ProfilesMap;
}

const baseURL = (contestJid: string) => `${baseContestURL(contestJid)}/managers`;

export const contestManagerAPI = {
  getManagers: (token: string, contestJid: string, page?: number): Promise<ContestManagersResponse> => {
    const params = stringify({ page });
    return get(`${baseURL(contestJid)}?${params}`, token);
  },

  upsertManagers: (token: string, contestJid: string, usernames: string[]): Promise<ContestManagersUpsertResponse> => {
    return post(`${baseURL(contestJid)}/batch-upsert`, token, usernames);
  },

  deleteManagers: (token: string, contestJid: string, usernames: string[]): Promise<ContestManagersDeleteResponse> => {
    return post(`${baseURL(contestJid)}/batch-delete`, token, usernames);
  },
};
