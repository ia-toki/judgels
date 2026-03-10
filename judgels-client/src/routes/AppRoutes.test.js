import { JerahmeelRole } from '../modules/api/jerahmeel/role';
import { JophielRole } from '../modules/api/jophiel/role';
import { getVisibleAppRoutes } from './AppRoutes';

describe('AppRoutes', () => {
  const testAppRoutes = (role, expectedIds) => {
    const appRoutes = getVisibleAppRoutes(role);
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
    testAppRoutes({ jophiel: JophielRole.Superadmin, jerahmeel: JerahmeelRole.Admin }, [
      'admin',
      'contests',
      'training',
      'courses',
      'problems',
      'submissions',
      'ranking',
    ]);
  });

  test('Jerahmeel admin', () => {
    testAppRoutes({ jerahmeel: JerahmeelRole.Admin }, [
      'contests',
      'training',
      'courses',
      'problems',
      'submissions',
      'ranking',
    ]);
  });

  test('user', () => {
    testAppRoutes({}, ['contests', 'courses', 'problems', 'submissions', 'ranking']);
  });
});
