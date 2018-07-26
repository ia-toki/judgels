import { AppState } from '../../../modules/store';

export function selectUserJid(state: AppState) {
  return state.jophiel.profile.userJid;
}
