import { BadRequestError, ForbiddenError } from '../../../../modules/api/error';
import { PutToken, PutUser } from '../../../../modules/session/sessionReducer';

export const loginActions = {
  logIn: (currentPath: string, username: string, password: string) => {
    return async (dispatch, getState, { legacySessionAPI, userAPI, toastActions }) => {
      let session;
      try {
        session = await legacySessionAPI.logIn(username, password);
      } catch (error) {
        if (error instanceof ForbiddenError) {
          throw new Error('Invalid username/password.');
        } else if (error instanceof BadRequestError) {
          throw new Error('For security reasons, please reset your password using the "Forgot password" link.');
        } else {
          throw error;
        }
      }

      const user = await userAPI.getMyself(session.token);
      toastActions.showToast(`Welcome, ${username}.`);
      dispatch(PutToken.create(session.token));
      dispatch(PutUser.create(user));

      legacySessionAPI.preparePostLogin(session.authCode, encodeURIComponent(currentPath));
    };
  },
};
