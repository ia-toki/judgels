import { courseChapterAPI } from '../../../../../../modules/api/jerahmeel/courseChapter';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { PutCourseChapters } from './courseChaptersReducer';

export function getChapters(courseJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await courseChapterAPI.getChapters(token, courseJid);
    dispatch(PutCourseChapters(response.data));
    return response;
  };
}
