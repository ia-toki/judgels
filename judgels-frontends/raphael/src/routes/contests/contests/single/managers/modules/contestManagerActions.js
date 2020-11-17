import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { contestManagerAPI } from '../../../../../../modules/api/uriel/contestManager';
import * as toastActions from '../../../../../../modules/toast/toastActions';

export function getManagers(contestJid: string, page?: number) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestManagerAPI.getManagers(token, contestJid, page);
  };
}

export function upsertManagers(contestJid: string, usernames: string[]) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestManagerAPI.upsertManagers(token, contestJid, usernames);
    if (Object.keys(response.insertedManagerProfilesMap).length === usernames.length) {
      toastActions.showSuccessToast('Managers added.');
    }
    return response;
  };
}

export function deleteManagers(contestJid: string, usernames: string[]) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestManagerAPI.deleteManagers(token, contestJid, usernames);
    if (Object.keys(response.deletedManagerProfilesMap).length === usernames.length) {
      toastActions.showSuccessToast('Managers removed.');
    }
    return response;
  };
}
