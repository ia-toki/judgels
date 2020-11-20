import { selectToken } from '../../../modules/session/sessionSelectors';
import { userInfoAPI } from '../../../modules/api/jophiel/userInfo';
import * as toastActions from '../../../modules/toast/toastActions';

export function getInfo(userJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await userInfoAPI.getInfo(token, userJid);
  };
}

export function updateInfo(userJid, info) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await userInfoAPI.updateInfo(token, userJid, info);

    toastActions.showSuccessToast('Info updated.');
  };
}
