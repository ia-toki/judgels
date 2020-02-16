import { UnauthorizedError } from '../../../../modules/api/error';
import { DelSession } from '../../../../modules/session/sessionReducer';
import { selectToken } from '../../../../modules/session/sessionSelectors';
import { sessionAPI } from '../../../../modules/api/jophiel/session';
import { legacySessionAPI } from '../../../../modules/api/jophiel/legacySession';

export function logOut(redirectUri: string) {
  return async (dispatch, getState) => {
    try {
      await sessionAPI.logOut(selectToken(getState()));
    } catch (error) {
      if (!(error instanceof UnauthorizedError)) {
        throw error;
      }
    }
    dispatch(DelSession.create());

    legacySessionAPI.postLogout(redirectUri);
  };
}
