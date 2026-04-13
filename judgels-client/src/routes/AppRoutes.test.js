import { JerahmeelRole } from '../modules/api/jerahmeel/role';
import { JophielRole } from '../modules/api/jophiel/role';
import { SandalphonRole } from '../modules/api/sandalphon/role';
import { UrielRole } from '../modules/api/uriel/role';
import { getVisibleAppRoutes } from './AppRoutes';
import { BLOCKED_USERNAMES } from './blockedUsernames';

describe('AppRoutes', () => {
  const testAppRoutes = (role, expectedIds, user = undefined) => {
    const appRoutes = getVisibleAppRoutes(role, user);
    const ids = appRoutes.map(route => route.id);
    expect(ids).toEqual(expectedIds);
  };

  test('Jophiel admin', () => {
    testAppRoutes({ jophiel: JophielRole.Admin }, [
      'admin',
      'contests',
      'courses',
      'problems',
      'submissions',
      'ranking',
    ]);
  });

  test('Jophiel superadmin', () => {
    testAppRoutes({ jophiel: JophielRole.Superadmin }, [
      'admin',
      'contests',
      'courses',
      'problems',
      'submissions',
      'ranking',
    ]);
  });

  test('Uriel admin', () => {
    testAppRoutes({ uriel: UrielRole.Admin }, ['admin', 'contests', 'courses', 'problems', 'submissions', 'ranking']);
  });

  test('Jerahmeel admin', () => {
    testAppRoutes({ jerahmeel: JerahmeelRole.Admin }, [
      'admin',
      'contests',
      'courses',
      'problems',
      'submissions',
      'ranking',
    ]);
  });

  test('user', () => {
    testAppRoutes({}, ['contests', 'courses', 'problems', 'submissions', 'ranking']);
  });

  test('blocked user', () => {
    const blockedUser = { username: BLOCKED_USERNAMES[0] };
    testAppRoutes({}, ['contests'], blockedUser);
  });
});
