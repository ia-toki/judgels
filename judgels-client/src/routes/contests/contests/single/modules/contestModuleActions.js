import { contestModuleAPI } from '../../../../../modules/api/uriel/contestModule';
import { getToken } from '../../../../../modules/session';

import * as toastActions from '../../../../../modules/toast/toastActions';

export async function getModules(contestJid) {
  const token = getToken();
  return await contestModuleAPI.getModules(token, contestJid);
}

export async function enableModule(contestJid, type) {
  const token = getToken();
  return await contestModuleAPI.enableModule(token, contestJid, type);
}

export async function disableModule(contestJid, type) {
  const token = getToken();
  return await contestModuleAPI.disableModule(token, contestJid, type);
}

export async function getConfig(contestJid) {
  const token = getToken();
  return await contestModuleAPI.getConfig(token, contestJid);
}

export async function upsertConfig(contestJid, config) {
  const token = getToken();
  await contestModuleAPI.upsertConfig(token, contestJid, config);
  toastActions.showSuccessToast('Configs updated.');
}
