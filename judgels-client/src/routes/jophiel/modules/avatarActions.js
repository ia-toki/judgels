import { userAvatarAPI } from '../../../modules/api/jophiel/userAvatar';
import { getToken } from '../../../modules/session';

import * as toastActions from '../../../modules/toast/toastActions';

export async function avatarExists(userJid) {
  return await userAvatarAPI.avatarExists(userJid);
}

export async function renderAvatar(userJid) {
  return await userAvatarAPI.renderAvatar(userJid);
}

export async function updateAvatar(userJid, file) {
  const token = getToken();
  await userAvatarAPI.updateAvatar(token, userJid, file);

  toastActions.showSuccessToast('Avatar updated.');
}

export async function deleteAvatar(userJid) {
  const token = getToken();
  await userAvatarAPI.deleteAvatar(token, userJid);

  toastActions.showSuccessToast('Avatar removed.');
}
