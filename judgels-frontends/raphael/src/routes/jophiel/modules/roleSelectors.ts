import { AppState } from 'modules/store';

export function selectRole(state: AppState) {
  return state.jophiel.role.value;
}
