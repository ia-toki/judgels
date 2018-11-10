import { Profile } from './profile';
import { JophielRole } from './role';
import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

export interface UserWebConfig {
  role: JophielRole;
  profile?: Profile;
}

export function createUserWebAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.jophiel}/user-web`;

  return {
    getWebConfig: (token: string): Promise<UserWebConfig> => {
      return get(`${baseURL}/config`, token);
    },
  };
}
