import { userAPI } from '../../../modules/api/jophiel/user';
import { getToken } from '../../../modules/session';

export async function getUser(userJid) {
  const token = getToken();
  return await userAPI.getUser(token, userJid);
}
