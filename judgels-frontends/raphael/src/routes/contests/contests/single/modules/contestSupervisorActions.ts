import { selectToken } from '../../../../../modules/session/sessionSelectors';
import { ContestSupervisorUpsertData, contestSupervisorAPI } from '../../../../../modules/api/uriel/contestSupervisor';
import * as toastActions from '../../../../../modules/toast/toastActions';

export function getSupervisors(contestJid: string, page?: number) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestSupervisorAPI.getSupervisors(token, contestJid, page);
  };
}

export function upsertSupervisors(contestJid: string, data: ContestSupervisorUpsertData) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestSupervisorAPI.upsertSupervisors(token, contestJid, data);
    if (Object.keys(response.upsertedSupervisorProfilesMap).length === data.usernames.length) {
      toastActions.showSuccessToast('Supervisors added.');
    }
    return response;
  };
}

export function deleteSupervisors(contestJid: string, usernames: string[]) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await contestSupervisorAPI.deleteSupervisors(token, contestJid, usernames);
    if (Object.keys(response.deletedSupervisorProfilesMap).length === usernames.length) {
      toastActions.showSuccessToast('Supervisors removed.');
    }
    return response;
  };
}
