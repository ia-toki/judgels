import { selectToken } from 'modules/session/sessionSelectors';

export const contestSupervisorActions = {
  getSupervisors: (contestJid: string, page?: number) => {
    return async (dispatch, getState, { contestSupervisorAPI }) => {
      const token = selectToken(getState());
      return await contestSupervisorAPI.getSupervisors(token, contestJid, page);
    };
  },

  upsertSupervisors: (contestJid: string, usernames: string[]) => {
    return async (dispatch, getState, { contestSupervisorAPI, toastActions }) => {
      const token = selectToken(getState());
      const response = await contestSupervisorAPI.upsertSupervisors(token, contestJid, usernames);
      if (Object.keys(response.upsertedSupervisorProfilesMap).length === usernames.length) {
        toastActions.showSuccessToast('Supervisors added.');
      }
      return response;
    };
  },

  deleteSupervisors: (contestJid: string, usernames: string[]) => {
    return async (dispatch, getState, { contestSupervisorAPI, toastActions }) => {
      const token = selectToken(getState());
      const response = await contestSupervisorAPI.deleteSupervisors(token, contestJid, usernames);
      if (Object.keys(response.deletedSupervisorProfilesMap).length === usernames.length) {
        toastActions.showSuccessToast('Supervisors removed.');
      }
      return response;
    };
  },
};
