import { APP_CONFIG } from '../../../conf';
import { post } from '../http';

export const SessionErrors = {
  UserNotActivated: 'Jophiel:UserNotActivated',
  UserMaxConcurrentSessionsExceeded: 'Jophiel:UserMaxConcurrentSessionsExceeded',
  LogoutDisabled: 'Jophiel:LogoutDisabled',
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
