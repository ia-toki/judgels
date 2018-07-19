import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';

export const contestContestantActions = {
  getMyContestantState: (contestJid: string) => {
    return async (dispatch, getState, { contestContestantAPI }) => {
      const token = selectToken(getState());
      return await contestContestantAPI.getMyContestantState(token, contestJid);
    };
  },

  getContestantsCount: (contestJid: string) => {
    return async (dispatch, getState, { contestContestantAPI }) => {
      const token = selectToken(getState());
      return await contestContestantAPI.getContestantsCount(token, contestJid);
    };
  },

  getContestants: (contestJid: string) => {
    return async (dispatch, getState, { contestContestantAPI }) => {
      const token = selectToken(getState());
      return await contestContestantAPI.getContestants(token, contestJid);
    };
  },

  registerMyselfAsContestant: (contestJid: string) => {
    return async (dispatch, getState, { contestContestantAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestContestantAPI.registerMyselfAsContestant(token, contestJid);
      toastActions.showSuccessToast('Successfully registered to the contest.');
    };
  },

  unregisterMyselfAsContestant: (contestJid: string) => {
    return async (dispatch, getState, { contestContestantAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestContestantAPI.unregisterMyselfAsContestant(token, contestJid);
      toastActions.showSuccessToast('Successfully unregistered from the contest.');
    };
  },
};
