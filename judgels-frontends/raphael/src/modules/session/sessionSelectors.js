export function selectIsLoggedIn(state) {
  return !!state.session.token;
}

export function selectToken(state) {
  return state.session && state.session.token;
}

export function selectUserJid(state) {
  return state.session.user.jid;
}

export function selectMaybeUserJid(state) {
  return state.session.user && state.session.user.jid;
}

export function selectMaybeUsername(state) {
  return state.session.user && state.session.user.username;
}
