import { AppState } from '../../../../modules/store';

export function selectPublicProfile(state: AppState) {
  return state.jophiel.publicProfile.value;
}
