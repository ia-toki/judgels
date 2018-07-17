import { APP_CONFIG } from '../../../conf';
import { post } from '../http';

export interface Session {
  token: string;
}

export function createSessionAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.jophiel}/session`;

  return {
    logIn: (username: string, password: string): Promise<Session> => {
      return post(`${baseURL}/login`, undefined, { username, password });
    },

    logOut: (token: string): Promise<void> => {
      return post(`${baseURL}/logout`, token);
    },
  };
}
