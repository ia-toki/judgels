import { selectToken } from '../../../../modules/session/sessionSelectors';

export const adminActions = {
  getAdmins: (page?: number) => {
    return async (dispatch, getState, { urielAdminAPI }) => {
      const token = selectToken(getState());
      return await urielAdminAPI.getAdmins(token, page);
    };
  },

  upsertAdmins: (usernames: string[]) => {
    return async (dispatch, getState, { urielAdminAPI, toastActions }) => {
      const token = selectToken(getState());
      const response = await urielAdminAPI.upsertAdmins(token, usernames);
      if (Object.keys(response.insertedAdminProfilesMap).length === usernames.length) {
        toastActions.showSuccessToast('Admins added.');
      }
      return response;
    };
  },

  deleteAdmins: (usernames: string[]) => {
    return async (dispatch, getState, { urielAdminAPI, toastActions }) => {
      const token = selectToken(getState());
      const response = await urielAdminAPI.deleteAdmins(token, usernames);
      if (Object.keys(response.deletedAdminProfilesMap).length === usernames.length) {
        toastActions.showSuccessToast('Admins removed.');
      }
      return response;
    };
  },
};
