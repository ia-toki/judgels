import { get } from '../http';

import { baseUserURL } from './user';

const baseURL = baseUserURL('me');

export const myUserAPI = {
  getMyself: token => {
    return get(`${baseURL}`, token);
  },
};
