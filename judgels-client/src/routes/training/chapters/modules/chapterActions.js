import { chapterAPI } from '../../../../modules/api/jerahmeel/chapter';
import { chapterLessonAPI } from '../../../../modules/api/jerahmeel/chapterLesson';
import { chapterProblemAPI } from '../../../../modules/api/jerahmeel/chapterProblem';
import { getToken } from '../../../../modules/session';

import * as toastActions from '../../../../modules/toast/toastActions';

export async function createChapter(data) {
  const token = getToken();
  await chapterAPI.createChapter(token, data);
  toastActions.showSuccessToast('Chapter created.');
}

export async function updateChapter(chapterJid, data) {
  const token = getToken();
  await chapterAPI.updateChapter(token, chapterJid, data);
  toastActions.showSuccessToast('Chapter updated.');
}

export async function getChapters() {
  const token = getToken();
  return await chapterAPI.getChapters(token);
}

export async function getProblems(chapterJid) {
  const token = getToken();
  return await chapterProblemAPI.getProblems(token, chapterJid);
}

export async function setProblems(chapterJid, data) {
  const token = getToken();
  await chapterProblemAPI.setProblems(token, chapterJid, data);
  toastActions.showSuccessToast('Chapter problems updated.');
}

export async function getLessons(chapterJid) {
  const token = getToken();
  return await chapterLessonAPI.getLessons(token, chapterJid);
}

export async function setLessons(chapterJid, data) {
  const token = getToken();
  await chapterLessonAPI.setLessons(token, chapterJid, data);
  toastActions.showSuccessToast('Chapter lessons updated.');
}
