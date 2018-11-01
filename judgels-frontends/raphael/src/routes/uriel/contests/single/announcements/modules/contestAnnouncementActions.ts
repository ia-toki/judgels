import { selectToken } from 'modules/session/sessionSelectors';
import { ContestAnnouncementData } from 'modules/api/uriel/contestAnnouncement';

export const contestAnnouncementActions = {
  getAnnouncements: (contestJid: string, page?: number) => {
    return async (dispatch, getState, { contestAnnouncementAPI }) => {
      const token = selectToken(getState());
      return await contestAnnouncementAPI.getAnnouncements(token, contestJid, page);
    };
  },

  alertNewAnnouncements: () => {
    return async (dispatch, getState, { toastActions }) => {
      toastActions.showAlertToast('You have new announcement(s).');
    };
  },

  createAnnouncement: (contestJid: string, data: ContestAnnouncementData) => {
    return async (dispatch, getState, { contestAnnouncementAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestAnnouncementAPI.createAnnouncement(token, contestJid, data);
      toastActions.showSuccessToast('Announcement created.');
    };
  },

  updateAnnouncement: (contestJid: string, announcementJid: string, data: ContestAnnouncementData) => {
    return async (dispatch, getState, { contestAnnouncementAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestAnnouncementAPI.updateAnnouncement(token, contestJid, announcementJid, data);
      toastActions.showSuccessToast('Announcement updated.');
    };
  },
};
