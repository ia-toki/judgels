import { get, post } from '../http';

import { baseUserURL } from './user';

const baseURL = baseUserURL('me');

export const myUserAPI = {
  getMyself: token => {
    return get(`${baseURL}`, token);
  },

  updateMyPassword: (token, data) => {
    return post(`${baseURL}/password`, token, data);
  },
};
