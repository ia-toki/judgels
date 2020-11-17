import { AppState } from '../../../modules/store';

export function selectIsUserWebConfigLoaded(state: AppState) {
  return state.jophiel.userWeb.isConfigLoaded;
}

export function selectRole(state: AppState) {
  return state.jophiel.userWeb.config.role;
}

export function selectUserProfile(state: AppState) {
  return state.jophiel.userWeb.config.profile;
}
