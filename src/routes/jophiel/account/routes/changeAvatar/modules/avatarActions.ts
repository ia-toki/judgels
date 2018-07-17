import { selectToken, selectUserJid } from '../../../../../../modules/session/sessionSelectors';
import { PutUser } from '../../../../../../modules/session/sessionReducer';
import { MAX_AVATAR_FILE_SIZE } from '../../../../panels/avatar/ChangeAvatar/ChangeAvatar';

export const avatarActions = {
  change: (file: File) => {
    return async (dispatch, getState, { userAPI, toastActions }) => {
      const token = selectToken(getState());
      const userJid = selectUserJid(getState());
      await userAPI.updateUserAvatar(token, userJid, file);

      const user = await userAPI.getMyself(token);
      dispatch(PutUser.create(user));

      toastActions.showSuccessToast('Avatar updated.');
    };
  },

  remove: () => {
    return async (dispatch, getState, { userAPI, toastActions }) => {
      const token = selectToken(getState());
      const userJid = selectUserJid(getState());
      await userAPI.deleteUserAvatar(token, userJid);

      const user = await userAPI.getMyself(token);
      dispatch(PutUser.create(user));

      toastActions.showSuccessToast('Avatar removed.');
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
