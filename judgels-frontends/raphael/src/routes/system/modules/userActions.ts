import { selectToken } from '../../../modules/session/sessionSelectors';
import { OrderDir } from '../../../modules/api/pagination';
import { userAPI } from '../../../modules/api/jophiel/user';

export function getUser(userJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await userAPI.getUser(token, userJid);
  };
}

export function getUsers(page: number, orderBy?: string, orderDir?: OrderDir) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await userAPI.getUsers(token, page, orderBy, orderDir);
  };
}
