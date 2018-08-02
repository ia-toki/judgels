import { UnauthorizedError } from 'modules/api/error';
import { DelSession } from 'modules/session/sessionReducer';
import { selectToken } from 'modules/session/sessionSelectors';
import { JophielRole } from 'modules/api/jophiel/my';

import { PutRole } from '../../modules/roleReducer';

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
      dispatch(PutRole.create(JophielRole.Guest));

      legacySessionAPI.postLogout(encodeURIComponent(currentPath));
    };
  },
};
