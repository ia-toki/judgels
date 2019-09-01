import { selectToken } from '../../../modules/session/sessionSelectors';

export const MAX_AVATAR_FILE_SIZE = 100 * 1024;

export const avatarActions = {
  avatarExists: (userJid?: string) => {
    return async (dispatch, getState, { userAvatarAPI }) => {
      return await userAvatarAPI.avatarExists(userJid);
    };
  },

  renderAvatar: (userJid?: string) => {
    return async (dispatch, getState, { userAvatarAPI }) => {
      return await userAvatarAPI.renderAvatar(userJid);
    };
  },

  updateAvatar: (userJid: string, file: File) => {
    return async (dispatch, getState, { userAvatarAPI, toastActions }) => {
      const token = selectToken(getState());
      await userAvatarAPI.updateAvatar(token, userJid, file);

      toastActions.showSuccessToast('Avatar updated.');
    };
  },

  deleteAvatar: (userJid: string) => {
    return async (dispatch, getState, { userAvatarAPI, toastActions }) => {
      const token = selectToken(getState());
      await userAvatarAPI.deleteAvatar(token, userJid);

      toastActions.showSuccessToast('Avatar removed.');
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
