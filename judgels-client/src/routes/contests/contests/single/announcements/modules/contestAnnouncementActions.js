import { showDesktopNotification } from '../../../../../../modules/notification/notification';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export async function alertNewAnnouncements(notificationTag) {
  const message = 'You have new announcement(s).';
  toastActions.showAlertToast(message);
  showDesktopNotification('New announcement(s)', notificationTag, message);
}
