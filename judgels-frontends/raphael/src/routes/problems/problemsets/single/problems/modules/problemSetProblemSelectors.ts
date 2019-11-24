import { AppState } from '../../../../../../modules/store';

export function selectProblemSetProblem(state: AppState) {
  return state.jerahmeel.problemSetProblem.value;
}
