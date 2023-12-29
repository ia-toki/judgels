import { contestSupervisorAPI } from '../../../../../modules/api/uriel/contestSupervisor';
import { selectToken } from '../../../../../modules/session/sessionSelectors';

import * as toastActions from '../../../../../modules/toast/toastActions';

export function getSupervisors(contestJid, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestSupervisorAPI.getSupervisors(token, contestJid, page);
  };
}

export function upsertSupervisors(contestJid, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestSupervisorAPI.upsertSupervisors(token, contestJid, data);
    if (Object.keys(response.upsertedSupervisorProfilesMap).length === data.usernames.length) {
      toastActions.showSuccessToast('Supervisors added.');
    }
    return response;
  };
}

export function deleteSupervisors(contestJid, usernames) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestSupervisorAPI.deleteSupervisors(token, contestJid, usernames);
    if (Object.keys(response.deletedSupervisorProfilesMap).length === usernames.length) {
      toastActions.showSuccessToast('Supervisors removed.');
    }
    return response;
  };
}
