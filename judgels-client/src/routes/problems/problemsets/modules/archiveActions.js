import { archiveAPI } from '../../../../modules/api/jerahmeel/archive';
import { selectToken } from '../../../../modules/session/sessionSelectors';

export function getArchives() {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await archiveAPI.getArchives(token);
  };
}
