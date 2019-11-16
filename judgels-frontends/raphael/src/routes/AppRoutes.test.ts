import { JophielRole } from '../modules/api/jophiel/role';

import { getVisibleAppRoutes } from './AppRoutes';

describe('AppRoutes', () => {
  const testAppRoutes = (role: JophielRole, expectedIds: Array<string>) => {
    const appRoutes = getVisibleAppRoutes(role);
    const ids = appRoutes.map(route => route.id);
    expect(ids).toEqual(expectedIds);
  };

  test('admin', () => {
    testAppRoutes(JophielRole.Admin, ['account', 'contests', 'courses', 'problems', 'ranking']);
  });

  test('superadmin', () => {
    testAppRoutes(JophielRole.Superadmin, ['account', 'contests', 'courses', 'problems', 'ranking']);
  });

  test('user', () => {
    testAppRoutes(JophielRole.User, ['contests', 'courses', 'problems', 'ranking']);
  });

  test('guest', () => {
    testAppRoutes(JophielRole.Guest, ['contests', 'courses', 'problems', 'ranking']);
  });
});
