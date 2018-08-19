import { AppState } from 'modules/store';

export function selectContest(state: AppState) {
  return state.uriel.contest.value;
}
