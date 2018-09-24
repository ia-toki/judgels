import { selectToken } from 'modules/session/sessionSelectors';
import { ContestClarificationData, ContestClarificationAnswerData,
  ContestClarificationStatus } from 'modules/api/uriel/contestClarification';

export const contestClarificationActions = {
  createClarification: (contestJid: string, data: ContestClarificationData) => {
    return async (dispatch, getState, { contestClarificationAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestClarificationAPI.createClarification(token, contestJid, data);
      toastActions.showSuccessToast('Clarification submitted.');
    };
  },

  getClarificationConfig: (contestJid: string, language: string) => {
    return async (dispatch, getState, { contestClarificationAPI }) => {
      const token = selectToken(getState());
      return await contestClarificationAPI.getClarificationConfig(token, contestJid, language);
    };
  },

  getClarifications: (contestJid: string, language: string) => {
    return async (dispatch, getState, { contestClarificationAPI }) => {
      const token = selectToken(getState());
      return await contestClarificationAPI.getClarifications(token, contestJid, language);
    };
  },

  alertNewClarifications: (status: ContestClarificationStatus) => {
    return async (dispatch, getState, { toastActions }) => {
      if (status === ContestClarificationStatus.Answered) {
        toastActions.showAlertToast('You have new answered clarification(s).');
      } else {
        toastActions.showAlertToast('You have new clarification(s).');
      }
    };
  },

  createAnswer: (contestJid: string, data: ContestClarificationAnswerData) => {
    return async (dispatch, getState, { contestClarificationAPI }) => {
      const token = selectToken(getState());
      return await contestClarificationAPI.createAnswer(token, contestJid, data);
    };
  },
};
