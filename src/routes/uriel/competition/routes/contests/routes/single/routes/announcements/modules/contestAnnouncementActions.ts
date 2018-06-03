import { selectToken } from '../../../../../../../../../../modules/session/sessionSelectors';

export const contestAnnouncementActions = {
  fetchList: (contestJid: string) => {
    return async (dispatch, getState, { contestAnnouncementAPI }) => {
      const token = selectToken(getState());
      return await contestAnnouncementAPI.getAnnouncements(token, contestJid);
    };
  },

  alertNew: () => {
    return async (dispatch, getState, { toastActions }) => {
      toastActions.showAlertToast('You have new notification(s).');
    };
  },
};
