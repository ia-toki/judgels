import { AppState } from '../store';

export function selectToken(state: AppState) {
  return state.session.token!;
}

export function selectUser(state: AppState) {
  return state.session.user!;
}

export function selectUserJid(state: AppState) {
  return state.session.user!.jid;
}

export function selectProfile(state: AppState) {
  return state.session.profile;
}
