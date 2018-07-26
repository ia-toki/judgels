import { APP_CONFIG } from '../../../conf';
import { get, post } from '../http';

export interface Profile {
  username?: string;
  rating?: number;
  nationality?: string;
}

export interface BasicProfile {
  username: string;
  rating?: number;
  name?: string;
  nationality?: string;
}

export interface ProfilesMap {
  [userJid: string]: Profile;
}

export function createProfileAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.jophiel}/profiles`;

  return {
    getProfiles: (userJids: string[]): Promise<Profile> => {
      return post(`${baseURL}`, undefined, userJids);
    },

    getBasicProfile: (userJid: string): Promise<BasicProfile> => {
      return get(`${baseURL}/${userJid}/basic`);
    },
  };
}
