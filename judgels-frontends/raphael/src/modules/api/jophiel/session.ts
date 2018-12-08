import { APP_CONFIG } from 'conf';
import { post } from 'modules/api/http';

export interface Session {
  token: string;
}

const baseUrl = `${APP_CONFIG.apiUrls.jophiel}/session`;

export const sessionAPI = {
  logIn: (username: string, password: string): Promise<Session> => {
    return post(`${baseUrl}/login`, undefined, { username, password });
  },

  logOut: (token: string): Promise<void> => {
    return post(`${baseUrl}/logout`, token);
  },
};
