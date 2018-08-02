import { selectToken } from 'modules/session/sessionSelectors';
import { UserInfo } from 'modules/api/jophiel/userInfo';

export const infoActions = {
  getInfo: (userJid: string) => {
    return async (dispatch, getState, { userInfoAPI }) => {
      const token = selectToken(getState());
      return await userInfoAPI.getInfo(token, userJid);
    };
  },

  updateInfo: (userJid: string, info: UserInfo) => {
    return async (dispatch, getState, { userInfoAPI, toastActions }) => {
      const token = selectToken(getState());
      await userInfoAPI.updateInfo(token, userJid, info);

      toastActions.showSuccessToast('Info updated.');
    };
  },
};
