import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

const baseURL = `${APP_CONFIG.apiUrls.jophiel}/user-web`;

export const userWebAPI = {
  getWebConfig: token => {
    return get(`${baseURL}/config`, token);
  },
};
