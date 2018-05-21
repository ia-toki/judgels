import { SessionState } from '../modules/session/sessionReducer';
import { User } from '../modules/api/jophiel/user';
import { Contest, ContestStyle } from '../modules/api/uriel/contest';

export const userJid = 'jid123';
export const user: User = { jid: userJid, username: 'user' };
export const token = 'token123';
export const sessionState: SessionState = {
  isLoggedIn: true,
  user,
  token,
};

export const contestJid = 'contestJid123';
export const contestId = '123';
export const contest: Contest = {
  id: 1,
  jid: contestJid,
  name: 'Contest',
  description: 'A contest',
  style: ContestStyle.ICPC,
  beginTime: 123,
  duration: 456,
};

export const problemJid = 'problemJid123';
