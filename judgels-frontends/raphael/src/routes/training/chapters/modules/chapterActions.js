import { selectToken } from '../../../../modules/session/sessionSelectors';
import { chapterAPI } from '../../../../modules/api/jerahmeel/chapter';
import { chapterProblemAPI } from '../../../../modules/api/jerahmeel/chapterProblem';
import { chapterLessonAPI } from '../../../../modules/api/jerahmeel/chapterLesson';
import * as toastActions from '../../../../modules/toast/toastActions';

export function createChapter(data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await chapterAPI.createChapter(token, data);
    toastActions.showSuccessToast('Chapter created.');
  };
}

export function updateChapter(chapterJid, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await chapterAPI.updateChapter(token, chapterJid, data);
    toastActions.showSuccessToast('Chapter updated.');
  };
}

export function getChapters() {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await chapterAPI.getChapters(token);
  };
}

export function getProblems(chapterJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await chapterProblemAPI.getProblems(token, chapterJid);
  };
}

export function setProblems(chapterJid, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await chapterProblemAPI.setProblems(token, chapterJid, data);
    toastActions.showSuccessToast('Chapter problems updated.');
  };
}

export function getLessons(chapterJid) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    return await chapterLessonAPI.getLessons(token, chapterJid);
  };
}

export function setLessons(chapterJid, data) {
  return async (dispatch, getState) => {
    const token = selectToken(getState());
    await chapterLessonAPI.setLessons(token, chapterJid, data);
    toastActions.showSuccessToast('Chapter lessons updated.');
  };
}
