// Temporary list of usernames blocked from accessing submissions, courses, and problems
// during selection tests. Remove this file when the restriction is no longer needed.
export const BLOCKED_USERNAMES = [];
//   'warren030310',
//   'mitchell.jh',
//   'thesen',
//   'Retr0Foxx',
//   'LC24',
//   'Andra_MHT_16',
//   'kalifpermadi',
//   'hitsuuj',
//   'marchell_hii',
//   'sakka',
//   'RKHTM',
//   'evanrusly',
//   'Jesslyn_nathania19',
//   'Bananade',
//   'yusuf12360',
// ];

export function isUserBlocked(user) {
  return BLOCKED_USERNAMES.includes(user?.username);
}
