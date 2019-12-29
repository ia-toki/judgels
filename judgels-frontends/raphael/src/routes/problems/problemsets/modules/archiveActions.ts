import { selectToken } from '../../../../modules/session/sessionSelectors';

export const archiveActions = {
  getArchives: () => {
    return async (dispatch, getState, { archiveAPI }) => {
      const token = selectToken(getState());
      return await archiveAPI.getArchives(token);
    };
  },
};
