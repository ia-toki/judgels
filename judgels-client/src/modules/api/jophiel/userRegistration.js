import { get } from '../http';

import { baseUsersURL } from './user';

const baseURL = `${baseUsersURL}/registration/web`;

export const userRegistrationWebAPI = {
  getWebConfig: () => {
    return get(`${baseURL}/config`);
  },
};
