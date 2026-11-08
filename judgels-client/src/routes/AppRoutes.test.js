import { UserRole } from '../modules/api/userRole';
import { userWebConfigQueryOptions } from '../modules/queries/userWeb';
import { queryClient } from '../modules/queryClient';
import { getVisibleAppRoutes } from './AppRoutes';

describe('AppRoutes', () => {
  const setTrainingFeatures = ({ trainingEnabled, ratingEnabled }) => {
    queryClient.setQueryData(userWebConfigQueryOptions().queryKey, { trainingEnabled, ratingEnabled });
  };

  beforeEach(() => {
    setTrainingFeatures({ trainingEnabled: true, ratingEnabled: true });
  });

  afterEach(() => {
    queryClient.clear();
  });

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

  test('training rating disabled', () => {
    setTrainingFeatures({ trainingEnabled: true, ratingEnabled: false });
    testAppRoutes([], ['contests', 'courses', 'problems', 'submissions']);
  });

  test('training disabled', () => {
    setTrainingFeatures({ trainingEnabled: false, ratingEnabled: false });
    testAppRoutes([UserRole.TrainingAdmin], ['contests']);
  });
});
