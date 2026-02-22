import { BadRequestError, ForbiddenError } from '../api/error';
import { SessionErrors, sessionAPI } from '../api/jophiel/session';
import { userAPI } from '../api/jophiel/user';
import { userWebConfigQueryOptions } from '../queries/userWeb';
import { queryClient } from '../queryClient';
import { clearSession, getToken, setSession } from '../session';

import * as toastActions from '../toast/toastActions';

export const logInMutationOptions = {
  mutationFn: async ({ usernameOrEmail, password }) => {
    let session;
    try {
      session = await sessionAPI.logIn(usernameOrEmail, password);
    } catch (error) {
      if (error instanceof ForbiddenError) {
        if (error.message === SessionErrors.UserNotActivated) {
          return { redirect: '/need-activation', email: error.args.email };
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
    return session;
  },
};

export async function afterLogin(session) {
  const user = await userAPI.getMyself(session.token);

  toastActions.showToast(`Welcome, ${user.username}.`);
  setSession(session.token, user);
  queryClient.invalidateQueries(userWebConfigQueryOptions());
}

export const logOutMutationOptions = {
  mutationFn: async () => {
    try {
      await sessionAPI.logOut(getToken());
    } catch (error) {
      if (error instanceof ForbiddenError) {
        if (error.message === SessionErrors.LogoutDisabled) {
          throw new Error('Logout is currently disabled.');
        }
      }
      throw error;
    }
    clearSession();
    queryClient.invalidateQueries(userWebConfigQueryOptions());
  },
};
