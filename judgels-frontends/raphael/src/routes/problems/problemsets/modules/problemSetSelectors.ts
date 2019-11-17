import { AppState } from '../../../../modules/store';

export function selectProblemSet(state: AppState) {
  return state.jerahmeel.problemSet.value;
}
