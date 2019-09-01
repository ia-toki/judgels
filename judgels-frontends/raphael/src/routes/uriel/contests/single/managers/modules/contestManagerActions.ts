import { selectToken } from '../../../../../../modules/session/sessionSelectors';

export const contestManagerActions = {
  getManagers: (contestJid: string, page?: number) => {
    return async (dispatch, getState, { contestManagerAPI }) => {
      const token = selectToken(getState());
      return await contestManagerAPI.getManagers(token, contestJid, page);
    };
  },

  upsertManagers: (contestJid: string, usernames: string[]) => {
    return async (dispatch, getState, { contestManagerAPI, toastActions }) => {
      const token = selectToken(getState());
      const response = await contestManagerAPI.upsertManagers(token, contestJid, usernames);
      if (Object.keys(response.insertedManagerProfilesMap).length === usernames.length) {
        toastActions.showSuccessToast('Managers added.');
      }
      return response;
    };
  },

  deleteManagers: (contestJid: string, usernames: string[]) => {
    return async (dispatch, getState, { contestManagerAPI, toastActions }) => {
      const token = selectToken(getState());
      const response = await contestManagerAPI.deleteManagers(token, contestJid, usernames);
      if (Object.keys(response.deletedManagerProfilesMap).length === usernames.length) {
        toastActions.showSuccessToast('Managers removed.');
      }
      return response;
    };
  },
};
