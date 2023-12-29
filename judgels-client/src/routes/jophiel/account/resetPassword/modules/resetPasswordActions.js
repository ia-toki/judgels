import { userAccountAPI } from '../../../../../modules/api/jophiel/userAccount';
import { selectUserEmail } from '../../../../../modules/session/sessionSelectors';

export function requestToResetPassword() {
  return async (dispatch, getState) => {
    const email = selectUserEmail(getState());
    await userAccountAPI.requestToResetPassword(email);
  };
}
