import { UnauthorizedError } from '../../../../modules/api/error';
import { DelSession } from '../../../../modules/session/sessionReducer';
import { selectToken } from '../../../../modules/session/sessionSelectors';

export const logoutActions = {
  logOut: (currentPath: string) => {
    return async (dispatch, getState, { sessionAPI, legacySessionAPI }) => {
      try {
        await sessionAPI.logOut(selectToken(getState()));
      } catch (error) {
        if (!(error instanceof UnauthorizedError)) {
          throw error;
        }
      }
      dispatch(DelSession.create());

      legacySessionAPI.postLogout(encodeURIComponent(currentPath));
    };
  },
};
