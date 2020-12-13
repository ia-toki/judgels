import { SubmissionError } from 'redux-form';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { ContestClarificationStatus } from '../../../../../../modules/api/uriel/contestClarification';
import { BadRequestError } from '../../../../../../modules/api/error';
import { ContestErrors } from '../../../../../../modules/api/uriel/contest';
import { contestClarificationAPI } from '../../../../../../modules/api/uriel/contestClarification';
import * as toastActions from '../../../../../../modules/toast/toastActions';
import { showDesktopNotification } from '../../../../../../modules/notification/notification';

export function createClarification(contestJid, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestClarificationAPI.createClarification(token, contestJid, data);
    toastActions.showSuccessToast('Clarification submitted.');
  };
}

export function getClarifications(contestJid, status, language, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestClarificationAPI.getClarifications(token, contestJid, status, language, page);
  };
}

export function answerClarification(contestJid, clarificationJid, answer) {
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

export function alertNewClarifications(status, notificationTag) {
  return async () => {
    let title, message;
    if (status === ContestClarificationStatus.Answered) {
      title = 'New answered clarification(s)';
      message = 'You have new answered clarification(s).';
    } else {
      title = 'New clarification(s)';
      message = 'You have new clarification(s).';
    }
    toastActions.showAlertToast(message);
    showDesktopNotification(title, notificationTag, message);
  };
}
