import { contestAnnouncementAPI } from '../../../../../../modules/api/uriel/contestAnnouncement';
import { showDesktopNotification } from '../../../../../../modules/notification/notification';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export function getAnnouncements(contestJid, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await contestAnnouncementAPI.getAnnouncements(token, contestJid, page);
  };
}

export function alertNewAnnouncements(notificationTag) {
  return async () => {
    const message = 'You have new announcement(s).';
    toastActions.showAlertToast(message);
    showDesktopNotification('New announcement(s)', notificationTag, message);
  };
}

export function createAnnouncement(contestJid, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestAnnouncementAPI.createAnnouncement(token, contestJid, data);
    toastActions.showSuccessToast('Announcement created.');
  };
}

export function updateAnnouncement(contestJid, announcementJid, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await contestAnnouncementAPI.updateAnnouncement(token, contestJid, announcementJid, data);
    toastActions.showSuccessToast('Announcement updated.');
  };
}
