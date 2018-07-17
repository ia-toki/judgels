import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';

export const contestContestantActions = {
  fetchMyState: (contestJid: string) => {
    return async (dispatch, getState, { contestContestantAPI }) => {
      const token = selectToken(getState());
      return await contestContestantAPI.getMyContestantState(token, contestJid);
    };
  },

  fetchCount: (contestJid: string) => {
    return async (dispatch, getState, { contestContestantAPI }) => {
      const token = selectToken(getState());
      return await contestContestantAPI.getContestantsCount(token, contestJid);
    };
  },

  fetchContestants: (contestJid: string) => {
    return async (dispatch, getState, { contestContestantAPI }) => {
      const token = selectToken(getState());
      return await contestContestantAPI.getContestants(token, contestJid);
    };
  },

  register: (contestJid: string) => {
    return async (dispatch, getState, { contestContestantAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestContestantAPI.register(token, contestJid);
      toastActions.showSuccessToast('Successfully registered to the contest.');
    };
  },

  unregister: (contestJid: string) => {
    return async (dispatch, getState, { contestContestantAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestContestantAPI.unregister(token, contestJid);
      toastActions.showSuccessToast('Successfully unregistered from the contest.');
    };
  },
};
