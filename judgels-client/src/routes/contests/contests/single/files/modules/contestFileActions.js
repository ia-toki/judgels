import { contestFileAPI } from '../../../../../../modules/api/uriel/contestFile';
import { getToken } from '../../../../../../modules/session';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export async function getFiles(contestJid) {
  const token = getToken();
  return await contestFileAPI.getFiles(token, contestJid);
}

export async function uploadFile(contestJid, file) {
  const token = getToken();
  await contestFileAPI.uploadFile(token, contestJid, file);

  toastActions.showSuccessToast('File uploaded.');
}
