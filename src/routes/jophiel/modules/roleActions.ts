import { selectToken } from '../../../modules/session/sessionSelectors';
import { PutRole } from './roleReducer';

export const roleActions = {
  get: () => {
    return async (dispatch, getState, { myAPI }) => {
      const token = selectToken(getState());
      const role = await myAPI.getRole(token);

      dispatch(PutRole.create(role));
    };
  },
};
