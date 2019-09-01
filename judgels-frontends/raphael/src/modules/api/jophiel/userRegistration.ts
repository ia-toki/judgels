import { get } from '../../../modules/api/http';

import { baseUsersURL } from './user';

export interface UserRegistrationWebConfig {
  useRecaptcha: boolean;
  recaptcha?: {
    siteKey: string;
  };
}

const baseURL = `${baseUsersURL}/registration/web`;

export const userRegistrationWebAPI = {
  getWebConfig: (): Promise<UserRegistrationWebConfig> => {
    return get(`${baseURL}/config`);
  },
};
