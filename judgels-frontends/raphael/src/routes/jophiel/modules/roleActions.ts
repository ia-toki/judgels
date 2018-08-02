import { selectToken } from 'modules/session/sessionSelectors';
import { JophielRole } from 'modules/api/jophiel/my';

import { PutRole } from './roleReducer';

export const roleActions = {
  getMyRole: () => {
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
