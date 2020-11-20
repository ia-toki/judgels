import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { chapterLessonAPI } from '../../../../../../../../modules/api/jerahmeel/chapterLesson';

export function getLessons(chapterJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await chapterLessonAPI.getLessons(token, chapterJid);
  };
}

export function getLessonStatement(chapterJid, lessonAlias, language) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await chapterLessonAPI.getLessonStatement(token, chapterJid, lessonAlias, language);
  };
}
