import { push } from 'connected-react-router';

import { ForbiddenError } from '../../../modules/api/error';
import { sessionAPI } from '../../../modules/api/jophiel/session';
import { userAccountAPI } from '../../../modules/api/jophiel/userAccount';
import { userSearchAPI } from '../../../modules/api/jophiel/userSearch';
import { SubmissionError } from '../../../modules/form/submissionError';
import { afterLogin } from '../login/modules/loginActions';

export function logIn(idToken) {
  return async dispatch => {
    let session;
    try {
      session = await sessionAPI.logInWithGoogle(idToken);
    } catch (error) {
      if (error instanceof ForbiddenError) {
        return false;
      }
      throw error;
    }
    await dispatch(afterLogin(session));
  };
}

export function register(data) {
  return async dispatch => {
    const usernameExists = await userSearchAPI.usernameExists(data.username);
    if (usernameExists) {
      throw new SubmissionError({ username: 'Username already exists' });
    }

    await userAccountAPI.registerGoogleUser(data);
    await dispatch(push('/registered?source=google'));
    await dispatch(logIn(data.idToken));
  };
}
