import { replace } from 'connected-react-router';

import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { chapterLessonAPI } from '../../../../../../../../modules/api/jerahmeel/chapterLesson';

export function redirectToLesson(baseURL, lessonAlias) {
  return async dispatch => {
    dispatch(replace(`${baseURL}/${lessonAlias}`));
  };
}

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
