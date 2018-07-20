import { selectToken, selectUserJid } from '../../../modules/session/sessionSelectors';
import { MAX_AVATAR_FILE_SIZE } from '../panels/avatar/ChangeAvatar/ChangeAvatar';

export const avatarActions = {
  renderAvatar: () => {
    return async (dispatch, getState, { userAvatarAPI }) => {
      const userJid = selectUserJid(getState());
      const avatarExists = await userAvatarAPI.avatarExists(userJid);
      if (avatarExists) {
        return await userAvatarAPI.renderAvatar(userJid);
      }
      return require('../../../assets/images/avatar-default.png');
    };
  },

  updateAvatar: (file: File) => {
    return async (dispatch, getState, { userAvatarAPI, toastActions }) => {
      const token = selectToken(getState());
      const userJid = selectUserJid(getState());
      await userAvatarAPI.updateAvatar(token, userJid, file);

      toastActions.showSuccessToast('Avatar updated.');
      window.location.reload();
    };
  },

  deleteAvatar: () => {
    return async (dispatch, getState, { userAvatarAPI, toastActions }) => {
      const token = selectToken(getState());
      const userJid = selectUserJid(getState());
      await userAvatarAPI.deleteAvatar(token, userJid);

      toastActions.showSuccessToast('Avatar removed.');
      window.location.reload();
    };
  },

  rejectAvatar: (file: File) => {
    return async (dispatch, getState, { toastActions }) => {
      if (file.size > MAX_AVATAR_FILE_SIZE) {
        toastActions.showErrorToast(new Error(`File too large (max ${MAX_AVATAR_FILE_SIZE / 1024} KB).`));
      }
    };
  },
};
