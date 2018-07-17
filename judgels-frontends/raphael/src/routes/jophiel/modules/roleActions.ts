import { selectToken } from '../../../modules/session/sessionSelectors';
import { PutRole } from './roleReducer';
import { JophielRole } from '../../../modules/api/jophiel/my';

export const roleActions = {
  get: () => {
    return async (dispatch, getState, { myAPI }) => {
      const token = selectToken(getState());
      let role;

      if (!token) {
        role = JophielRole.Guest;
      } else {
        role = await myAPI.getMyRole(token);
      }

      dispatch(PutRole.create(role));
    };
  },
};
