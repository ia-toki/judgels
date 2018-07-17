import { APP_CONFIG } from '../../../conf';
import { post } from '../http';

export interface LegacySession {
  authCode: string;
  token: string;
  userJid: string;
}

export function createLegacySessionAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.legacyJophiel}/session`;

  return {
    logIn: (username: string, password: string): Promise<LegacySession> => {
      return post(`${baseURL}/login`, undefined, { username, password });
    },

    preparePostLogin: (authCode: string, redirectUri: string) => {
      window.location.replace(`${baseURL}/prepare-post-login/${authCode}/${redirectUri}`);
    },

    propagateLogin: (token: string) => {
      return post(`${baseURL}/propagate-login`, token);
    },

    postLogout: (redirectUri: string) => {
      window.location.replace(`${baseURL}/post-logout/${redirectUri}`);
    },
  };
}
