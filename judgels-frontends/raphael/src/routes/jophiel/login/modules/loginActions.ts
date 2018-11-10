import { BadRequestError, ForbiddenError } from 'modules/api/error';
import { PutToken, PutUser } from 'modules/session/sessionReducer';

import { PutWebConfig } from '../../modules/userWebReducer';

export const loginActions = {
  logIn: (currentPath: string, usernameOrEmail: string, password: string) => {
    return async (dispatch, getState, { legacySessionAPI, myUserAPI, userWebAPI, toastActions }) => {
      let session;
      try {
        session = await legacySessionAPI.logIn(usernameOrEmail, password);
      } catch (error) {
        if (error instanceof ForbiddenError) {
          throw new Error('Invalid username/password.');
        } else if (error instanceof BadRequestError) {
          throw new Error('For security reasons, please reset your password using the "Forgot password" link.');
        } else {
          throw error;
        }
      }

      const { token } = session;
      const [user, config] = await Promise.all([myUserAPI.getMyself(session.token), userWebAPI.getWebConfig(token)]);

      toastActions.showToast(`Welcome, ${user.username}.`);
      dispatch(PutToken.create(session.token));
      dispatch(PutUser.create(user));
      dispatch(PutWebConfig.create(config));

      legacySessionAPI.preparePostLogin(session.authCode, encodeURIComponent(currentPath));
    };
  },
};
