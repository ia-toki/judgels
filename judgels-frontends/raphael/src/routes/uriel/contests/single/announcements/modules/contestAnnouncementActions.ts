import { selectToken } from 'modules/session/sessionSelectors';
import { ContestAnnouncementData } from 'modules/api/uriel/contestAnnouncement';

export const contestAnnouncementActions = {
  getAllAnnouncements: (contestJid: string) => {
    return async (dispatch, getState, { contestAnnouncementAPI }) => {
      const token = selectToken(getState());
      return await contestAnnouncementAPI.getAllAnnouncements(token, contestJid);
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

  getAnnouncementConfig: (contestJid: string) => {
    return async (dispatch, getState, { contestAnnouncementAPI }) => {
      const token = selectToken(getState());
      return await contestAnnouncementAPI.getAnnouncementConfig(token, contestJid);
    };
  },
};
