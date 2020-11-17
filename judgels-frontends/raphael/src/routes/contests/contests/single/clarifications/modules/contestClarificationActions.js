import { SubmissionError } from 'redux-form';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import {
  ContestClarificationData,
  ContestClarificationStatus,
} from '../../../../../../modules/api/uriel/contestClarification';
import { BadRequestError } from '../../../../../../modules/api/error';
import { ContestErrors } from '../../../../../../modules/api/uriel/contest';
import { contestClarificationAPI } from '../../../../../../modules/api/uriel/contestClarification';
import * as toastActions from '../../../../../../modules/toast/toastActions';

export function createClarification(contestJid: string, data: ContestClarificationData) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestClarificationAPI.createClarification(token, contestJid, data);
    toastActions.showSuccessToast('Clarification submitted.');
  };
}

export function getClarifications(contestJid: string, status?: string, language?: string, page?: number) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestClarificationAPI.getClarifications(token, contestJid, status, language, page);
  };
}

export function answerClarification(contestJid: string, clarificationJid: string, answer: string) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    try {
      await contestClarificationAPI.answerClarification(token, contestJid, clarificationJid, { answer });
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ContestErrors.ClarificationAlreadyAnswered) {
        throw new SubmissionError({
          _error: 'This clarification has already been answered. Please refresh this page.',
        });
      }
      throw error;
    }
  };
}

export function alertNewClarifications(status: ContestClarificationStatus) {
  return async () => {
    if (status === ContestClarificationStatus.Answered) {
      toastActions.showAlertToast('You have new answered clarification(s).');
    } else {
      toastActions.showAlertToast('You have new clarification(s).');
    }
  };
}
