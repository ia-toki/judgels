import { ForbiddenError } from '../../../modules/api/error';
import { sessionAPI } from '../../../modules/api/jophiel/session';
import { userAccountAPI } from '../../../modules/api/jophiel/userAccount';
import { userSearchAPI } from '../../../modules/api/jophiel/userSearch';
import { SubmissionError } from '../../../modules/form/submissionError';
import { getNavigationRef } from '../../../modules/navigation/navigationRef';

import { afterLogin } from '../login/modules/loginActions';

export async function logIn(idToken) {
  let session;
  try {
    session = await sessionAPI.logInWithGoogle(idToken);
  } catch (error) {
    if (error instanceof ForbiddenError) {
      return false;
    }
    throw error;
  }
  await afterLogin(session);
}

export async function register(data) {
  const usernameExists = await userSearchAPI.usernameExists(data.username);
  if (usernameExists) {
    throw new SubmissionError({ username: 'Username already exists' });
  }

  await userAccountAPI.registerGoogleUser(data);
  getNavigationRef().push('/registered?source=google');
  await logIn(data.idToken);
}
