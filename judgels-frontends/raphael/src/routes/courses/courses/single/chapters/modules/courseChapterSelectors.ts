import { AppState } from '../../../../../../modules/store';

export function selectCourseChapter(state: AppState) {
  return state.jerahmeel.courseChapter;
}
