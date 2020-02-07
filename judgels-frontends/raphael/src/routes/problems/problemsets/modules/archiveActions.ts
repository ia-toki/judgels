import { selectToken } from '../../../../modules/session/sessionSelectors';
import { archiveAPI } from '../../../../modules/api/jerahmeel/archive';

export const archiveActions = {
  getArchives: () => {
    return async (dispatch, getState) => {
      const token = selectToken(getState());
      return await archiveAPI.getArchives(token);
    };
  },
};
