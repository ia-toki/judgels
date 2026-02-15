import { userInfoAPI } from '../../../modules/api/jophiel/userInfo';
import { getToken } from '../../../modules/session';

import * as toastActions from '../../../modules/toast/toastActions';

export async function getInfo(userJid) {
  const token = getToken();
  return await userInfoAPI.getInfo(token, userJid);
}

export async function updateInfo(userJid, info) {
  const token = getToken();
  await userInfoAPI.updateInfo(token, userJid, info);

  toastActions.showSuccessToast('Info updated.');
}
