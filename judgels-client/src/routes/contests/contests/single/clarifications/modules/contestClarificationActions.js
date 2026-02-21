import { ContestClarificationStatus } from '../../../../../../modules/api/uriel/contestClarification';
import { showDesktopNotification } from '../../../../../../modules/notification/notification';

import * as toastActions from '../../../../../../modules/toast/toastActions';

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
