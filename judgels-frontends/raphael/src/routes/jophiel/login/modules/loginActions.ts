import { replace } from 'connected-react-router';

import { BadRequestError, ForbiddenError } from '../../../../modules/api/error';
import { PutToken, PutUser } from '../../../../modules/session/sessionReducer';
import { PutWebConfig } from '../../modules/userWebReducer';
import { SessionErrors } from '../../../../modules/api/jophiel/session';
import { legacySessionAPI } from '../../../../modules/api/jophiel/legacySession';
import { myUserAPI } from '../../../../modules/api/jophiel/myUser';
import { userWebAPI } from '../../../../modules/api/jophiel/userWeb';
import * as toastActions from '../../../../modules/toast/toastActions';

export function logIn(currentPath: string, usernameOrEmail: string, password: string) {
  return async dispatch => {
    let session;
    try {
      session = await legacySessionAPI.logIn(usernameOrEmail, password);
    } catch (error) {
      if (error instanceof ForbiddenError) {
        if (error.message === SessionErrors.UserNotActivated) {
          dispatch(replace('/need-activation', { email: error.parameters.email }));
          return;
        } else {
          throw new Error('Invalid username/password.');
        }
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
}
