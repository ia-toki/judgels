import { get, post } from '../http';

const baseURL = `/api/v2/user-search`;

export const userSearchAPI = {
  usernameExists: username => {
    return get(`${baseURL}/username-exists/${username}`);
  },

  emailExists: email => {
    return get(`${baseURL}/email-exists/${email}`);
  },

  translateUsernamesToJids: usernames => {
    return post(`${baseURL}/username-to-jid`, undefined, usernames);
  },
};
