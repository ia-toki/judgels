import { get } from '../http';

const baseURL = `/api/v2/user-web`;

export const userWebAPI = {
  getWebConfig: token => {
    return get(`${baseURL}/config`, token);
  },
};
