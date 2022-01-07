import { replace } from 'connected-react-router';

import { BadRequestError, ForbiddenError } from '../../../../modules/api/error';
import { PutToken, PutUser } from '../../../../modules/session/sessionReducer';
import { PutWebConfig } from '../../modules/userWebReducer';
import { SessionErrors } from '../../../../modules/api/jophiel/session';
import { sessionAPI } from '../../../../modules/api/jophiel/session';
import { myUserAPI } from '../../../../modules/api/jophiel/myUser';
import { userWebAPI } from '../../../../modules/api/jophiel/userWeb';
import * as toastActions from '../../../../modules/toast/toastActions';

export function logIn(usernameOrEmail, password) {
  return async dispatch => {
    let session;
    try {
      session = await sessionAPI.logIn(usernameOrEmail, password);
    } catch (error) {
      if (error instanceof ForbiddenError) {
        if (error.message === SessionErrors.UserNotActivated) {
          dispatch(replace('/need-activation', { email: error.args.email }));
          return;
        } else if (error.message === SessionErrors.UserMaxConcurrentSessionsExceeded) {
          throw new Error('Login failed because you are trying to log in from too many places at once.');
        } else {
          throw new Error('Invalid username/password.');
        }
      } else if (error instanceof BadRequestError) {
        throw new Error('For security reasons, please reset your password using the "Forgot password" link.');
      } else {
        throw error;
      }
    }
    await dispatch(afterLogin(session));
  };
}

export function afterLogin(session) {
  return async dispatch => {
    const { token } = session;
    const [user, config] = await Promise.all([myUserAPI.getMyself(session.token), userWebAPI.getWebConfig(token)]);

    toastActions.showToast(`Welcome, ${user.username}.`);
    dispatch(PutToken(session.token));
    dispatch(PutUser(user));
    dispatch(PutWebConfig(config));
  };
}
