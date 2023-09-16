import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { PutCourseChapter, DelCourseChapter } from './courseChapterReducer';
import { PutCourseChapters } from './courseChaptersReducer';
import { courseChapterAPI } from '../../../../../../modules/api/jerahmeel/courseChapter';

export function getChapters(courseJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const response = await courseChapterAPI.getChapters(token, courseJid);
    dispatch(PutCourseChapters(response.data));
    return response;
  };
}

export function getChapter(courseJid, courseSlug, chapterAlias) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const chapter = await courseChapterAPI.getChapter(token, courseJid, chapterAlias);
    dispatch(
      PutCourseChapter({
        ...chapter,
        alias: chapterAlias,
        courseSlug,
      })
    );
    return chapter;
  };
}

export const clearChapter = DelCourseChapter;
