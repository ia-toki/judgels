import { SessionState } from 'modules/session/sessionReducer';
import { User } from 'modules/api/jophiel/user';
import { Contest, ContestStyle } from 'modules/api/uriel/contest';

export const userJid = 'jid123';
export const user: User = { jid: userJid, username: 'user' };
export const token = 'token123';
export const sessionState: SessionState = {
  isLoggedIn: true,
  user,
  token,
};

export const contestId = '123';
export const contestJid = 'contestJid123';
export const contestSlug = 'contest-a';
export const contestName = 'Contest A';
export const contestBeginTime = 123456;
export const contestDuration = 5 * 60 * 60 * 1000;
export const contestStyle = ContestStyle.ICPC;
export const contest: Contest = {
  id: 1,
  jid: contestJid,
  slug: contestSlug,
  name: contestName,
  style: contestStyle,
  beginTime: contestBeginTime,
  duration: contestDuration,
};

export const problemJid = 'problemJid123';
export const announcementJid = 'announcementJid123';
