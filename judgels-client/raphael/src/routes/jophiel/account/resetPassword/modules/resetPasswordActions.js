import { selectUserEmail } from '../../../../../modules/session/sessionSelectors';
import { userAccountAPI } from '../../../../../modules/api/jophiel/userAccount';

export function requestToResetPassword() {
  return async (dispatch, getState) => {
    const email = selectUserEmail(getState());
    await userAccountAPI.requestToResetPassword(email);
  };
}
