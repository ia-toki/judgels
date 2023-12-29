import { contestLogAPI } from '../../../../../../modules/api/uriel/contestLog';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';

export function getLogs(contestJid, username, problemAlias, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestLogAPI.getLogs(token, contestJid, username, problemAlias, page);
  };
}
