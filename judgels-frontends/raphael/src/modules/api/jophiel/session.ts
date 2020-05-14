import { APP_CONFIG } from '../../../conf';
import { post } from '../../../modules/api/http';

export interface Session {
  token: string;
}

export enum SessionErrors {
  UserNotActivated = 'Jophiel:UserNotActivated',
}

const baseUrl = `${APP_CONFIG.apiUrls.jophiel}/session`;

export const sessionAPI = {
  logIn: (usernameOrEmail: string, password: string): Promise<Session> => {
    return post(`${baseUrl}/login`, undefined, { usernameOrEmail, password });
  },

  logOut: (token: string): Promise<void> => {
    return post(`${baseUrl}/logout`, token);
  },
};
