import { AppState } from '../../../modules/store';

export function selectUserJid(state: AppState) {
  return state.jophiel.profile.userJid;
}

export function selectUsername(state: AppState) {
  return state.jophiel.profile.username;
}
