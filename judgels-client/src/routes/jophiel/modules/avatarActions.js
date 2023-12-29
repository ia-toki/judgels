import { userAvatarAPI } from '../../../modules/api/jophiel/userAvatar';
import { selectToken } from '../../../modules/session/sessionSelectors';

import * as toastActions from '../../../modules/toast/toastActions';

export function avatarExists(userJid) {
  return async () => {
    return await userAvatarAPI.avatarExists(userJid);
  };
}

export function renderAvatar(userJid) {
  return async () => {
    return await userAvatarAPI.renderAvatar(userJid);
  };
}

export function updateAvatar(userJid, file) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await userAvatarAPI.updateAvatar(token, userJid, file);

    toastActions.showSuccessToast('Avatar updated.');
  };
}

export function deleteAvatar(userJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await userAvatarAPI.deleteAvatar(token, userJid);

    toastActions.showSuccessToast('Avatar removed.');
  };
}
