import { userAccountAPI } from '../../../../../modules/api/jophiel/userAccount';
import { getUser } from '../../../../../modules/session';

export async function requestToResetPassword() {
  const email = getUser().email;
  await userAccountAPI.requestToResetPassword(email);
}
