import { APP_CONFIG } from 'conf';
import { post } from 'modules/api/http';

export interface LegacySession {
  authCode: string;
  token: string;
  userJid: string;
}

const baseUrl = `${APP_CONFIG.apiUrls.legacyJophiel}/session`;

export const legacySessionAPI = {
  logIn: (usernameOrEmail: string, password: string): Promise<LegacySession> => {
    return post(`${baseUrl}/login`, undefined, { usernameOrEmail, password });
  },

  preparePostLogin: (authCode: string, redirectUri: string) => {
    setTimeout(() => {
      window.location.replace(`${baseUrl}/prepare-post-login/${authCode}/${redirectUri}`);
    }, 500);
  },

  propagateLogin: (token: string) => {
    return post(`${baseUrl}/propagate-login`, token);
  },

  postLogout: (redirectUri: string) => {
    setTimeout(() => {
      window.location.replace(`${baseUrl}/post-logout/${redirectUri}`);
    }, 500);
  },
};
