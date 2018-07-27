import { JophielRole } from '../modules/api/jophiel/my';
import { getAppRoutes } from './AppRoutes';

describe('AppRoutes', () => {
  const testAppRoutes = (role: JophielRole, expectedIds: Array<string>) => {
    const appRoutes = getAppRoutes(role);
    const ids = appRoutes.map(route => route.id);
    expect(ids).toEqual(expectedIds);
  };

  test('admin', () => {
    testAppRoutes(JophielRole.Admin, ['account', 'contests', 'ranking', 'training']);
  });

  test('superadmin', () => {
    testAppRoutes(JophielRole.Superadmin, ['account', 'contests', 'ranking', 'training']);
  });

  test('user', () => {
    testAppRoutes(JophielRole.User, ['contests', 'ranking', 'training']);
  });

  test('guest', () => {
    testAppRoutes(JophielRole.Guest, ['contests', 'ranking', 'training']);
  });
});
