import { selectToken } from '../../../../modules/session/sessionSelectors';
import { urielAdminAPI } from '../../../../modules/api/uriel/admin';
import * as toastActions from '../../../../modules/toast/toastActions';

export function getAdmins(page?: number) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await urielAdminAPI.getAdmins(token, page);
  };
}

export function upsertAdmins(usernames: string[]) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await urielAdminAPI.upsertAdmins(token, usernames);
    if (Object.keys(response.insertedAdminProfilesMap).length === usernames.length) {
      toastActions.showSuccessToast('Admins added.');
    }
    return response;
  };
}

export function deleteAdmins(usernames: string[]) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await urielAdminAPI.deleteAdmins(token, usernames);
    if (Object.keys(response.deletedAdminProfilesMap).length === usernames.length) {
      toastActions.showSuccessToast('Admins removed.');
    }
    return response;
  };
}
