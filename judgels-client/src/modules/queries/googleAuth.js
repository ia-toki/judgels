import { ForbiddenError } from '../api/error';
import { sessionAPI } from '../api/jophiel/session';
import { userAccountAPI } from '../api/jophiel/userAccount';
import { userSearchAPI } from '../api/jophiel/userSearch';
import { SubmissionError } from '../form/submissionError';
import { afterLogin } from './session';

export const googleLogInMutationOptions = {
  mutationFn: async idToken => {
    let session;
    try {
      session = await sessionAPI.logInWithGoogle(idToken);
    } catch (error) {
      if (error instanceof ForbiddenError) {
        return null;
      }
      throw error;
    }
    await afterLogin(session);
    return session;
  },
};

export const googleRegisterMutationOptions = {
  mutationFn: async data => {
    const usernameExists = await userSearchAPI.usernameExists(data.username);
    if (usernameExists) {
      throw new SubmissionError({ username: 'Username already exists' });
    }

    await userAccountAPI.registerGoogleUser(data);
  },
};
