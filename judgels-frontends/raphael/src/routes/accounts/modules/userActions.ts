import { selectToken } from '../../../modules/session/sessionSelectors';
import { OrderDir } from '../../../modules/api/pagination';

export const userActions = {
  getUser: (userJid: string) => {
    return async (dispatch, getState, { userAPI }) => {
      const token = selectToken(getState());
      return await userAPI.getUser(token, userJid);
    };
  },

  getUsers: (page: number, orderBy?: string, orderDir?: OrderDir) => {
    return async (dispatch, getState, { userAPI }) => {
      const token = selectToken(getState());
      return await userAPI.getUsers(token, page, orderBy, orderDir);
    };
  },
};
