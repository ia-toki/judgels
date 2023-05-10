import { selectToken } from '../../../../../modules/session/sessionSelectors';
import { contestModuleAPI } from '../../../../../modules/api/uriel/contestModule';
import * as toastActions from '../../../../../modules/toast/toastActions';

export function getModules(contestJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestModuleAPI.getModules(token, contestJid);
  };
}

export function enableModule(contestJid, type) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestModuleAPI.enableModule(token, contestJid, type);
  };
}

export function disableModule(contestJid, type) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestModuleAPI.disableModule(token, contestJid, type);
  };
}

export function getConfig(contestJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestModuleAPI.getConfig(token, contestJid);
  };
}

export function upsertConfig(contestJid, config) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestModuleAPI.upsertConfig(token, contestJid, config);
    toastActions.showSuccessToast('Configs updated.');
  };
}
