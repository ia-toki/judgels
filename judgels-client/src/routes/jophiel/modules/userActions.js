import { selectToken } from '../../../modules/session/sessionSelectors';
import { userAPI } from '../../../modules/api/jophiel/user';

export function getUser(userJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await userAPI.getUser(token, userJid);
  };
}
