import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import {
  ContestClarificationData,
  ContestClarificationStatus,
} from '../../../../../../modules/api/uriel/contestClarification';
import { BadRequestError } from '../../../../../../modules/api/error';
import { ContestErrors } from '../../../../../../modules/api/uriel/contest';
import { SubmissionError } from 'redux-form';

export const contestClarificationActions = {
  createClarification: (contestJid: string, data: ContestClarificationData) => {
    return async (dispatch, getState, { contestClarificationAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestClarificationAPI.createClarification(token, contestJid, data);
      toastActions.showSuccessToast('Clarification submitted.');
    };
  },

  getClarifications: (contestJid: string, language?: string, page?: number) => {
    return async (dispatch, getState, { contestClarificationAPI }) => {
      const token = selectToken(getState());
      return await contestClarificationAPI.getClarifications(token, contestJid, language, page);
    };
  },

  answerClarification: (contestJid: string, clarificationJid: string, answer: string, isEdit?: boolean) => {
    return async (dispatch, getState, { contestClarificationAPI }) => {
      const token = selectToken(getState());
      try {
        await contestClarificationAPI.answerClarification(token, contestJid, clarificationJid, { answer, isEdit });
      } catch (error) {
        if (error instanceof BadRequestError && error.message === ContestErrors.ClarificationAlreadyAnswered) {
          throw new SubmissionError({ _error: 'This clarification already has an answer' });
        }
        throw error;
      }
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
};
