import { UserRole } from '../modules/api/userRole';
import { getVisibleAppRoutes } from './AppRoutes';

describe('AppRoutes', () => {
  const testAppRoutes = (roles, expectedIds) => {
    const appRoutes = getVisibleAppRoutes(roles);
    const ids = appRoutes.map(route => route.id);
    expect(ids).toEqual(expectedIds);
  };

  test('system admin', () => {
    testAppRoutes([UserRole.SystemAdmin], ['admin', 'contests', 'courses', 'problems', 'submissions', 'ranking']);
  });

  test('superadmin', () => {
    testAppRoutes([UserRole.Superadmin], ['admin', 'contests', 'courses', 'problems', 'submissions', 'ranking']);
  });

  test('contest admin', () => {
    testAppRoutes([UserRole.ContestAdmin], ['admin', 'contests', 'courses', 'problems', 'submissions', 'ranking']);
  });

  test('problem admin', () => {
    testAppRoutes([UserRole.ProblemAdmin], ['admin', 'contests', 'courses', 'problems', 'submissions', 'ranking']);
  });

  test('problem writer', () => {
    testAppRoutes([UserRole.ProblemWriter], ['admin', 'contests', 'courses', 'problems', 'submissions', 'ranking']);
  });

  test('training admin', () => {
    testAppRoutes([UserRole.TrainingAdmin], ['admin', 'contests', 'courses', 'problems', 'submissions', 'ranking']);
  });

  test('user', () => {
    testAppRoutes([], ['contests', 'courses', 'problems', 'submissions', 'ranking']);
  });
});
