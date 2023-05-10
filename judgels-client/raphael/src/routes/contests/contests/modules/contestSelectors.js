export function selectContest(state) {
  return state.uriel.contest.value;
}

export function selectIsEditingContest(state) {
  return !!state.uriel.contest.isEditing;
}
