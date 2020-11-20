import { selectToken } from '../../../modules/session/sessionSelectors';
import { userAPI } from '../../../modules/api/jophiel/user';

export function getUser(userJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await userAPI.getUser(token, userJid);
  };
}

export function getUsers(page, orderBy, orderDir) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await userAPI.getUsers(token, page, orderBy, orderDir);
  };
}
