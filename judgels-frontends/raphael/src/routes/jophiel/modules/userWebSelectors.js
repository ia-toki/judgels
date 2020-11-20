export function selectIsUserWebConfigLoaded(state) {
  return state.jophiel.userWeb.isConfigLoaded;
}

export function selectRole(state) {
  return state.jophiel.userWeb.config.role;
}

export function selectUserProfile(state) {
  return state.jophiel.userWeb.config.profile;
}
