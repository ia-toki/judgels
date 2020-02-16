import { UnauthorizedError } from '../../../../modules/api/error';
import { JophielRole } from '../../../../modules/api/jophiel/role';
import { DelSession } from '../../../../modules/session/sessionReducer';
import { selectToken } from '../../../../modules/session/sessionSelectors';
import { PutWebConfig } from '../../modules/userWebReducer';
import { sessionAPI } from '../../../../modules/api/jophiel/session';
import { legacySessionAPI } from '../../../../modules/api/jophiel/legacySession';

export function logOut(currentPath: string) {
  return async (dispatch, getState) => {
    try {
      await sessionAPI.logOut(selectToken(getState()));
    } catch (error) {
      if (!(error instanceof UnauthorizedError)) {
        throw error;
      }
    }
    dispatch(DelSession.create());
    dispatch(PutWebConfig.create({ role: JophielRole.Guest }));

    legacySessionAPI.postLogout(encodeURIComponent(currentPath));
  };
}
