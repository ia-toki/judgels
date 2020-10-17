import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

export interface WebConfig {
  announcements: string[];
}

const baseURL = `${APP_CONFIG.apiUrls.jophiel}/web`;

export const webAPI = {
  getConfig: (): Promise<WebConfig> => {
    return get(`${baseURL}/config`);
  },
};
