import { APP_CONFIG } from '../../conf';
import { post } from './http';

export const SessionErrors = {
  UserNotActivated: 'UserNotActivated',
  UserMaxConcurrentSessionsExceeded: 'UserMaxConcurrentSessionsExceeded',
  LogoutDisabled: 'LogoutDisabled',
};

const baseUrl = `${APP_CONFIG.apiUrl}/session`;

export const sessionAPI = {
  logIn: (usernameOrEmail, password) => {
    return post(`${baseUrl}/login`, undefined, { usernameOrEmail, password });
  },

  logInWithGoogle: idToken => {
    return post(`${baseUrl}/login-google`, undefined, { idToken });
  },

  logOut: token => {
    return post(`${baseUrl}/logout`, token);
  },
};
