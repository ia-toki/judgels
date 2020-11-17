import { APP_CONFIG } from '../../../conf';
import { get, post } from '../../../modules/api/http';

export interface UsernamesMap {
  [username: string]: string;
}

const baseURL = `${APP_CONFIG.apiUrls.jophiel}/user-search`;

export const userSearchAPI = {
  usernameExists: (username: string): Promise<boolean> => {
    return get(`${baseURL}/username-exists/${username}`);
  },

  emailExists: (email: string): Promise<boolean> => {
    return get(`${baseURL}/email-exists/${email}`);
  },

  translateUsernamesToJids: (usernames: string[]): Promise<UsernamesMap> => {
    return post(`${baseURL}/username-to-jid`, undefined, usernames);
  },
};
