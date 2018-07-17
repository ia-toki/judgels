import { selectToken, selectUserJid } from '../../../../../../modules/session/sessionSelectors';
import { MAX_AVATAR_FILE_SIZE } from '../../../../panels/avatar/ChangeAvatar/ChangeAvatar';

export const avatarActions = {
  render: () => {
    return async (dispatch, getState, { userAvatarAPI }) => {
      const userJid = selectUserJid(getState());
      const avatarExists = await userAvatarAPI.avatarExists(userJid);
      if (avatarExists) {
        return await userAvatarAPI.renderAvatar(userJid);
      }
    };
  },

  change: (file: File) => {
    return async (dispatch, getState, { userAvatarAPI, toastActions }) => {
      const token = selectToken(getState());
      const userJid = selectUserJid(getState());
      await userAvatarAPI.updateAvatar(token, userJid, file);

      toastActions.showSuccessToast('Avatar updated.');
      window.location.reload();
    };
  },

  remove: () => {
    return async (dispatch, getState, { userAvatarAPI, toastActions }) => {
      const token = selectToken(getState());
      const userJid = selectUserJid(getState());
      await userAvatarAPI.deleteAvatar(token, userJid);

      toastActions.showSuccessToast('Avatar removed.');
      window.location.reload();
    };
  },

  reject: (file: File) => {
    return async (dispatch, getState, { toastActions }) => {
      if (file.size > MAX_AVATAR_FILE_SIZE) {
        toastActions.showErrorToast(new Error(`File too large (max ${MAX_AVATAR_FILE_SIZE / 1024} KB).`));
      }
    };
  },
};
