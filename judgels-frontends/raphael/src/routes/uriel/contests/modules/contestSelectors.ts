import { AppState } from 'modules/store';

export function selectContest(state: AppState) {
  return state.uriel.contest.value;
}

export function selectContestDescription(state: AppState) {
  return state.uriel.contestDescription.value;
}