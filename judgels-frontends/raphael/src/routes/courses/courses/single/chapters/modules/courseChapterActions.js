import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { PutCourseChapter, DelCourseChapter } from './courseChapterReducer';
import { courseChapterAPI } from '../../../../../../modules/api/jerahmeel/courseChapter';

export function getChapters(courseJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await courseChapterAPI.getChapters(token, courseJid);
  };
}

export function getChapter(courseJid, courseSlug, chapterAlias) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    const chapter = await courseChapterAPI.getChapter(token, courseJid, chapterAlias);
    dispatch(
      PutCourseChapter({
        value: { alias: chapterAlias, chapterJid: chapter.jid },
        courseSlug,
        name: chapter.name,
      })
    );
    return chapter;
  };
}

export const clearChapter = DelCourseChapter;
