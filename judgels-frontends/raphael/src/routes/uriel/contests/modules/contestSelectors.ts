import { AppState } from 'modules/store';

export function selectContest(state: AppState) {
  return state.uriel.contest.value;
}

export function selectIsEditingContest(state: AppState): boolean {
  return !!state.uriel.contest.isEditing;
}
