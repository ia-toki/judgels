import { selectToken } from 'modules/session/sessionSelectors';
import { ContestModuleType } from 'modules/api/uriel/contestModule';

export const contestModuleActions = {
  getModules: (contestJid: string) => {
    return async (dispatch, getState, { contestModuleAPI }) => {
      const token = selectToken(getState());
      return await contestModuleAPI.getModules(token, contestJid);
    };
  },

  enableModule: (contestJid: string, type: ContestModuleType) => {
    return async (dispatch, getState, { contestModuleAPI }) => {
      const token = selectToken(getState());
      return await contestModuleAPI.enableModule(token, contestJid, type);
    };
  },

  disableModule: (contestJid: string, type: ContestModuleType) => {
    return async (dispatch, getState, { contestModuleAPI }) => {
      const token = selectToken(getState());
      return await contestModuleAPI.disableModule(token, contestJid, type);
    };
  },
};
