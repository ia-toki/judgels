import { selectToken } from '../../../../../modules/session/sessionSelectors';
import {
  ContestModulesConfig,
  ContestModuleType,
  contestModuleAPI,
} from '../../../../../modules/api/uriel/contestModule';
import * as toastActions from '../../../../../modules/toast/toastActions';

export function getModules(contestJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestModuleAPI.getModules(token, contestJid);
  };
}

export function enableModule(contestJid: string, type: ContestModuleType) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestModuleAPI.enableModule(token, contestJid, type);
  };
}

export function disableModule(contestJid: string, type: ContestModuleType) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestModuleAPI.disableModule(token, contestJid, type);
  };
}

export function getConfig(contestJid: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestModuleAPI.getConfig(token, contestJid);
  };
}

export function upsertConfig(contestJid: string, config: ContestModulesConfig) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestModuleAPI.upsertConfig(token, contestJid, config);
    toastActions.showSuccessToast('Configs updated.');
  };
}
