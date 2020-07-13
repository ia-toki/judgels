import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { contestLogAPI } from '../../../../../../modules/api/uriel/contestLog';

export function getLogs(contestJid: string, userJid?: string, problemJid?: string, page?: number) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestLogAPI.getLogs(token, contestJid, userJid, problemJid, page);
  };
}
