import { ArchiveAdminRole } from '../modules/api/archiveAdminRole';
import { ContestAdminRole } from '../modules/api/contestAdminRole';
import { ProblemAdminRole } from '../modules/api/problemAdminRole';
import { UserAdminRole } from '../modules/api/userAdminRole';
import { getVisibleAppRoutes } from './AppRoutes';

describe('AppRoutes', () => {
  const testAppRoutes = (role, expectedIds) => {
    const appRoutes = getVisibleAppRoutes(role);
    const ids = appRoutes.map(route => route.id);
    expect(ids).toEqual(expectedIds);
  };

  test('Jophiel admin', () => {
    testAppRoutes({ jophiel: UserAdminRole.Admin }, [
      'admin',
      'contests',
      'courses',
      'problems',
      'submissions',
      'ranking',
    ]);
  });

  test('Jophiel superadmin', () => {
    testAppRoutes({ jophiel: UserAdminRole.Superadmin }, [
      'admin',
      'contests',
      'courses',
      'problems',
      'submissions',
      'ranking',
    ]);
  });

  test('Uriel admin', () => {
    testAppRoutes({ uriel: ContestAdminRole.Admin }, [
      'admin',
      'contests',
      'courses',
      'problems',
      'submissions',
      'ranking',
    ]);
  });

  test('Jerahmeel admin', () => {
    testAppRoutes({ jerahmeel: ArchiveAdminRole.Admin }, [
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
});
