import { stringify } from 'query-string';

import { APP_CONFIG } from 'conf';
import { get, post } from 'modules/api/http';
import { Page } from 'modules/api/pagination';
import { ProfilesMap } from 'modules/api/jophiel/profile';

export interface ContestManager {
  userJid: string;
}

export interface ContestManagersResponse {
  data: Page<ContestManager>;
  profilesMap: ProfilesMap;
}

export interface ContestManagerUpsertResponse {
  insertedManagerProfilesMap: ProfilesMap;
  alreadyManagerProfilesMap: ProfilesMap;
}

export interface ContestManagerDeleteResponse {
  deletedManagerProfilesMap: ProfilesMap;
}

export function createContestManagerAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getManagers: (token: string, contestJid: string, page?: number): Promise<ContestManagersResponse> => {
      const params = stringify({ page });
      return get(`${baseURL}/${contestJid}/managers?${params}`, token);
    },

    upsertManagers: (token: string, contestJid: string, usernames: string[]): Promise<ContestManagerUpsertResponse> => {
      return post(`${baseURL}/${contestJid}/managers/batch-upsert`, token, usernames);
    },

    deleteManagers: (token: string, contestJid: string, usernames: string[]): Promise<ContestManagerDeleteResponse> => {
      return post(`${baseURL}/${contestJid}/managers/batch-delete`, token, usernames);
    },
  };
}
