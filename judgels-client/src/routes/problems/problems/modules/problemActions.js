import { problemAPI } from '../../../../modules/api/jerahmeel/problem';
import { selectToken } from '../../../../modules/session/sessionSelectors';

export function getProblems(tags, page) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await problemAPI.getProblems(token, tags, page);
  };
}

export function getProblemTags() {
  return async () => {
    return await problemAPI.getProblemTags();
  };
}
