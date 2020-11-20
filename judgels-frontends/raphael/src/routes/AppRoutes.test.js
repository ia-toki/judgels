import { JophielRole } from '../modules/api/jophiel/role';
import { JerahmeelRole } from '../modules/api/jerahmeel/role';

import { getVisibleAppRoutes } from './AppRoutes';

describe('AppRoutes', () => {
  const testAppRoutes = (role, expectedIds) => {
    const appRoutes = getVisibleAppRoutes(role);
    const ids = appRoutes.map(route => route.id);
    expect(ids).toEqual(expectedIds);
  };

  test('Jophiel admin', () => {
    testAppRoutes({ jophiel: JophielRole.Admin }, [
      'system',
      'contests',
      'courses',
      'problems',
      'submissions',
      'ranking',
    ]);
  });

  test('Jophiel superadmin', () => {
    testAppRoutes({ jophiel: JophielRole.Superadmin, jerahmeel: JerahmeelRole.Admin }, [
      'system',
      'contests',
      'training',
      'courses',
      'problems',
      'submissions',
      'ranking',
    ]);
  });

  test('Jerahmeel admin', () => {
    testAppRoutes({ jophiel: JophielRole.User, jerahmeel: JerahmeelRole.Admin }, [
      'contests',
      'training',
      'courses',
      'problems',
      'submissions',
      'ranking',
    ]);
  });

  test('user', () => {
    testAppRoutes({ jophiel: JophielRole.User }, ['contests', 'courses', 'problems', 'submissions', 'ranking']);
  });

  test('guest', () => {
    testAppRoutes({ jophiel: JophielRole.Guest }, ['contests', 'courses', 'problems', 'submissions', 'ranking']);
  });
});
