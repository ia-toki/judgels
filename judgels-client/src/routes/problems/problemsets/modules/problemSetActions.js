import { problemSetAPI } from '../../../../modules/api/jerahmeel/problemSet';
import { selectToken } from '../../../../modules/session/sessionSelectors';

export function getProblemSets(archiveSlug, name, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await problemSetAPI.getProblemSets(token, archiveSlug, name, page);
  };
}
