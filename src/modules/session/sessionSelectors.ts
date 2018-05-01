import { AppState } from '../store';

export function selectToken(state: AppState) {
  return state.session.token!;
}

export function selectUserJid(state: AppState) {
  return state.session.user!.jid;
}

export function selectMaybeUserJid(state: AppState) {
  return state.session.user && state.session.user.jid;
}

export function selectProfile(state: AppState) {
  return state.session.profile;
}
