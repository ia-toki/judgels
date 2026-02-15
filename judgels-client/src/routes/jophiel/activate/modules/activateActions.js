import { userAccountAPI } from '../../../../modules/api/jophiel/userAccount';

export async function activateUser(emailCode) {
  await userAccountAPI.activateUser(emailCode);
}
