import { selectToken } from '../../../../../../../../../../modules/session/sessionSelectors';

export const contestClarificationActions = {
  fetchMyList: (contestJid: string, language: string) => {
    return async (dispatch, getState, { contestClarificationAPI }) => {
      const token = selectToken(getState());
      return await contestClarificationAPI.getMyClarifications(token, contestJid, language);
    };
  },

  alertNewAnswered: () => {
    return async (dispatch, getState, { toastActions }) => {
      toastActions.showAlertToast('You have new answered clarification(s).');
    };
  },
};
