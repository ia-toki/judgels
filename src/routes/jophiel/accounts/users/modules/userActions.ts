import { selectToken } from '../../../../../modules/session/sessionSelectors';

export const userActions = {
  fetchList: (page: number) => {
    return async (dispatch, getState, { userAPI }) => {
      const token = selectToken(getState());
      return await userAPI.getUsers(token, page);
    };
  },
};
