import { stringify } from 'query-string';

import { APP_CONFIG } from 'conf';
import { get, post } from 'modules/api/http';
import { Page } from 'modules/api/pagination';
import { ProfilesMap } from 'modules/api/jophiel/profile';

export interface Admin {
  userJid: string;
}

export interface AdminsResponse {
  data: Page<Admin>;
  profilesMap: ProfilesMap;
}

export interface AdminUpsertResponse {
  insertedAdminProfilesMap: ProfilesMap;
  alreadyAdminProfilesMap: ProfilesMap;
}

export interface AdminDeleteResponse {
  deletedAdminProfilesMap: ProfilesMap;
}

export function createUrielAdminAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/admins`;

  return {
    getAdmins: (token: string, page?: number): Promise<AdminsResponse> => {
      const params = stringify({ page });
      return get(`${baseURL}?${params}`, token);
    },

    upsertAdmins: (token: string, usernames: string[]): Promise<AdminUpsertResponse> => {
      return post(`${baseURL}/batch-upsert`, token, usernames);
    },

    deleteAdmins: (token: string, usernames: string[]): Promise<AdminDeleteResponse> => {
      return post(`${baseURL}/batch-delete`, token, usernames);
    },
  };
}
