import { SessionState } from '../modules/session/sessionReducer';
import { User } from '../modules/api/jophiel/user';

export const userJid = 'jid123';
export const user: User = { jid: userJid, username: 'user' };
export const token = 'token123';
export const sessionState: SessionState = {
  isLoggedIn: true,
  user,
  token,
};
