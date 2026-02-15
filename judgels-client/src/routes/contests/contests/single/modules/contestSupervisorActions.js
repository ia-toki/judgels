import { contestSupervisorAPI } from '../../../../../modules/api/uriel/contestSupervisor';
import { getToken } from '../../../../../modules/session';

import * as toastActions from '../../../../../modules/toast/toastActions';

export async function getSupervisors(contestJid, page) {
  const token = getToken();
  return await contestSupervisorAPI.getSupervisors(token, contestJid, page);
}

export async function upsertSupervisors(contestJid, data) {
  const token = getToken();
  const response = await contestSupervisorAPI.upsertSupervisors(token, contestJid, data);
  if (Object.keys(response.upsertedSupervisorProfilesMap).length === data.usernames.length) {
    toastActions.showSuccessToast('Supervisors added.');
  }
  return response;
}

export async function deleteSupervisors(contestJid, usernames) {
  const token = getToken();
  const response = await contestSupervisorAPI.deleteSupervisors(token, contestJid, usernames);
  if (Object.keys(response.deletedSupervisorProfilesMap).length === usernames.length) {
    toastActions.showSuccessToast('Supervisors removed.');
  }
  return response;
}
