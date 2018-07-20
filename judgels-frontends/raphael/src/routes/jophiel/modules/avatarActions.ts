import { selectToken } from '../../../modules/session/sessionSelectors';

export const MAX_AVATAR_FILE_SIZE = 100 * 1024;

export const avatarActions = {
  renderAvatar: (userJid?: string) => {
    return async (dispatch, getState, { userAvatarAPI }) => {
      let avatarExists = false;
      if (userJid) {
        avatarExists = avatarExists || (await userAvatarAPI.avatarExists(userJid));
      }
      if (avatarExists) {
        return await userAvatarAPI.renderAvatar(userJid);
      }
      return require('../../../assets/images/avatar-default.png');
    };
  },

  updateAvatar: (userJid: string, file: File) => {
    return async (dispatch, getState, { userAvatarAPI, toastActions }) => {
      const token = selectToken(getState());
      await userAvatarAPI.updateAvatar(token, userJid, file);

      toastActions.showSuccessToast('Avatar updated.');
      window.location.reload();
    };
  },

  deleteAvatar: (userJid: string) => {
    return async (dispatch, getState, { userAvatarAPI, toastActions }) => {
      const token = selectToken(getState());
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
