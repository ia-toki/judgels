import { contestManagerAPI } from '../../../../../../modules/api/uriel/contestManager';
import { getToken } from '../../../../../../modules/session';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export async function getManagers(contestJid, page) {
  const token = getToken();
  return await contestManagerAPI.getManagers(token, contestJid, page);
}

export async function upsertManagers(contestJid, usernames) {
  const token = getToken();
  const response = await contestManagerAPI.upsertManagers(token, contestJid, usernames);
  if (Object.keys(response.insertedManagerProfilesMap).length === usernames.length) {
    toastActions.showSuccessToast('Managers added.');
  }
  return response;
}

export async function deleteManagers(contestJid, usernames) {
  const token = getToken();
  const response = await contestManagerAPI.deleteManagers(token, contestJid, usernames);
  if (Object.keys(response.deletedManagerProfilesMap).length === usernames.length) {
    toastActions.showSuccessToast('Managers removed.');
  }
  return response;
}
