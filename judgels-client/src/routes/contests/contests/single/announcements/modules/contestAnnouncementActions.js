import { contestAnnouncementAPI } from '../../../../../../modules/api/uriel/contestAnnouncement';
import { showDesktopNotification } from '../../../../../../modules/notification/notification';
import { getToken } from '../../../../../../modules/session';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export async function getAnnouncements(contestJid, page) {
  const token = getToken();
  return await contestAnnouncementAPI.getAnnouncements(token, contestJid, page);
}

export async function alertNewAnnouncements(notificationTag) {
  const message = 'You have new announcement(s).';
  toastActions.showAlertToast(message);
  showDesktopNotification('New announcement(s)', notificationTag, message);
}

export async function createAnnouncement(contestJid, data) {
  const token = getToken();
  await contestAnnouncementAPI.createAnnouncement(token, contestJid, data);
  toastActions.showSuccessToast('Announcement created.');
}

export async function updateAnnouncement(contestJid, announcementJid, data) {
  const token = getToken();
  await contestAnnouncementAPI.updateAnnouncement(token, contestJid, announcementJid, data);
  toastActions.showSuccessToast('Announcement updated.');
}
