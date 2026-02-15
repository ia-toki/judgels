import { BadRequestError } from '../../../../../../modules/api/error';
import { ContestErrors } from '../../../../../../modules/api/uriel/contest';
import { ContestClarificationStatus } from '../../../../../../modules/api/uriel/contestClarification';
import { contestClarificationAPI } from '../../../../../../modules/api/uriel/contestClarification';
import { showDesktopNotification } from '../../../../../../modules/notification/notification';
import { getToken } from '../../../../../../modules/session';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export async function createClarification(contestJid, data) {
  const token = getToken();
  await contestClarificationAPI.createClarification(token, contestJid, data);
  toastActions.showSuccessToast('Clarification submitted.');
}

export async function getClarifications(contestJid, status, language, page) {
  const token = getToken();
  return await contestClarificationAPI.getClarifications(token, contestJid, status, language, page);
}

export async function answerClarification(contestJid, clarificationJid, answer) {
  const token = getToken();
  try {
    await contestClarificationAPI.answerClarification(token, contestJid, clarificationJid, { answer });
  } catch (error) {
    if (error instanceof BadRequestError && error.message === ContestErrors.ClarificationAlreadyAnswered) {
      throw new Error('This clarification has already been answered. Please refresh this page.');
    }
    throw error;
  }
}

export async function alertNewClarifications(status, notificationTag) {
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
}
