import { BadRequestError } from '../../../../../../modules/api/error';
import { ContestErrors } from '../../../../../../modules/api/uriel/contest';
import { ContestClarificationStatus } from '../../../../../../modules/api/uriel/contestClarification';
import { contestClarificationAPI } from '../../../../../../modules/api/uriel/contestClarification';
import { showDesktopNotification } from '../../../../../../modules/notification/notification';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';

import * as toastActions from '../../../../../../modules/toast/toastActions';

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
        throw new Error('This clarification has already been answered. Please refresh this page.');
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
