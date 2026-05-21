// Temporary list of usernames blocked from accessing submissions, courses, and problems
// during selection tests. Remove this file when the restriction is no longer needed.
export const BLOCKED_USERNAMES = [
  'aufan',
  'Retr0Foxx',
  'Bananade',
  'kalifpermadi',
  'LC24',
  'mitchell.jh',
  'ByeWorld',
  'yusuf12360',
  'thesen',
  'warren030310',
];

export function isUserBlocked(user) {
  return BLOCKED_USERNAMES.includes(user?.username);
}
