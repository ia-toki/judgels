import { APP_CONFIG } from 'conf';
import { get } from 'modules/api/http';

export interface UserRegistrationWebConfig {
  useRecaptcha: boolean;
  recaptcha?: {
    siteKey: string;
  };
}

export function createUserRegistrationWebAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.jophiel}/user-registration/web`;

  return {
    getConfig: (): Promise<UserRegistrationWebConfig> => {
      return get(`${baseURL}/config`);
    },
  };
}
