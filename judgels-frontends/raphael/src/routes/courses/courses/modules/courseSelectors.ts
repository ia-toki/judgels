import { AppState } from '../../../../modules/store';

export function selectCourse(state: AppState) {
  return state.jerahmeel.course.value;
}
