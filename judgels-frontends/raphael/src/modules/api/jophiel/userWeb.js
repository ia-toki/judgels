import { Profile } from './profile';
import { UserRole } from './role';
import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

export interface UserWebConfig {
  role: UserRole;
  profile?: Profile;
}

const baseURL = `${APP_CONFIG.apiUrls.jophiel}/user-web`;

export const userWebAPI = {
  getWebConfig: (token: string): Promise<UserWebConfig> => {
    return get(`${baseURL}/config`, token);
  },
};
