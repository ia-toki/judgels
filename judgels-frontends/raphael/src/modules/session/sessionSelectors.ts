import { AppState } from '../../modules/store';

export function selectIsLoggedIn(state: AppState) {
  return state.session.isLoggedIn;
}

export function selectToken(state: AppState) {
  return state.session && state.session.token;
}

export function selectUserJid(state: AppState) {
  return state.session.user!.jid;
}

export function selectMaybeUserJid(state: AppState) {
  return state.session.user && state.session.user.jid;
}
