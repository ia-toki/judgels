import { AppState } from '../../../../../../modules/store';

export function selectCourseChapter(state: AppState) {
  return state.jerahmeel.courseChapter.value;
}

export function selectCourseChapterName(state: AppState) {
  return state.jerahmeel.courseChapter.name;
}
