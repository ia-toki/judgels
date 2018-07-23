import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';

export const contestAnnouncementActions = {
  getPublishedAnnouncements: (contestJid: string) => {
    return async (dispatch, getState, { contestAnnouncementAPI }) => {
      const token = selectToken(getState());
      return await contestAnnouncementAPI.getPublishedAnnouncements(token, contestJid);
    };
  },

  alertNewAnnouncements: () => {
    return async (dispatch, getState, { toastActions }) => {
      toastActions.showAlertToast('You have new announcement(s).');
    };
  },
};
