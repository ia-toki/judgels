import { courseAPI } from '../../../../modules/api/jerahmeel/course';
import { selectToken } from '../../../../modules/session/sessionSelectors';

export function getCourses() {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await courseAPI.getCourses(token);
  };
}
