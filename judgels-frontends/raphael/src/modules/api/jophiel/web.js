import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

const baseURL = `${APP_CONFIG.apiUrls.jophiel}/web`;

export const webAPI = {
  getConfig: () => {
    return get(`${baseURL}/config`);
  },
};
