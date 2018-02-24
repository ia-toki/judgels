import { selectToken, selectUserJid } from '../../../modules/session/sessionSelectors';
import { PutRole } from './roleReducer';

export const roleActions = {
  get: () => {
    return async (dispatch, getState, { userAPI }) => {
      const token = selectToken(getState());
      const userJid = selectUserJid(getState());
      const role = await userAPI.getRole(token, userJid);

      dispatch(PutRole.create(role));
    };
  },
};
