import { ForbiddenError } from 'modules/api/error';
import { PutToken, PutUser } from 'modules/session/sessionReducer';
import { selectToken } from 'modules/session/sessionSelectors';

export const serviceLoginActions = {
  logIn: (usernameOrEmail: string, password: string, redirectUri: string, returnUri: string) => {
    return async (dispatch, getState, { legacySessionAPI, myAPI }) => {
      let session;
      try {
        session = await legacySessionAPI.logIn(usernameOrEmail, password);
      } catch (error) {
        if (error instanceof ForbiddenError) {
          throw new Error('Invalid username/password.');
        } else {
          throw error;
        }
      }

      const user = await myAPI.getMyself(session.token);
      dispatch(PutToken.create(session.token));
      dispatch(PutUser.create(user));

      const nextRedirectUri = encodeURIComponent(`${decodeURIComponent(redirectUri)}/${session.authCode}/${returnUri}`);
      legacySessionAPI.preparePostLogin(session.authCode, nextRedirectUri);
    };
  },

  propagateLogin: (redirectUri: string, returnUri: string) => {
    return async (dispatch, getState, { legacySessionAPI }) => {
      const token = selectToken(getState());
      const session = await legacySessionAPI.propagateLogin(token);

      const nextRedirectUri = encodeURIComponent(`${decodeURIComponent(redirectUri)}/${session.authCode}/${returnUri}`);
      legacySessionAPI.preparePostLogin(session.authCode, nextRedirectUri);
    };
  },
};
