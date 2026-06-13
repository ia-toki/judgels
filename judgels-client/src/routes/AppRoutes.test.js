import { ContestAdminRole } from '../modules/api/contestAdminRole';
import { ProblemAdminRole } from '../modules/api/problemAdminRole';
import { TrainingAdminRole } from '../modules/api/trainingAdminRole';
import { UserAdminRole } from '../modules/api/userAdminRole';
import { getVisibleAppRoutes } from './AppRoutes';

describe('AppRoutes', () => {
  const testAppRoutes = (role, expectedIds) => {
    const appRoutes = getVisibleAppRoutes(role);
    const ids = appRoutes.map(route => route.id);
    expect(ids).toEqual(expectedIds);
  };

  test('Jophiel admin', () => {
    testAppRoutes({ account: UserAdminRole.Admin }, [
      'admin',
      'contests',
      'courses',
      'problems',
      'submissions',
      'ranking',
    ]);
  });

  test('Jophiel superadmin', () => {
    testAppRoutes({ account: UserAdminRole.Superadmin }, [
      'admin',
      'contests',
      'courses',
      'problems',
      'submissions',
      'ranking',
    ]);
  });

  test('Uriel admin', () => {
    testAppRoutes({ contest: ContestAdminRole.Admin }, [
      'admin',
      'contests',
      'courses',
      'problems',
      'submissions',
      'ranking',
    ]);
  });

  test('Jerahmeel admin', () => {
    testAppRoutes({ training: TrainingAdminRole.Admin }, [
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
