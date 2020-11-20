import { APP_CONFIG } from '../../../conf';
import { get, post } from '../http';

const baseURL = `${APP_CONFIG.apiUrls.jophiel}/user-search`;

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
