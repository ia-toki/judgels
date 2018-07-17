import { JophielRole } from '../modules/api/jophiel/my';
import { getAppRoutes } from './AppRoutes';

describe('AppRoutes', () => {
  const testAppRoutes = (role: JophielRole, expectedIds: Array<string>) => {
    const appRoutes = getAppRoutes(role);
    const ids = appRoutes.map(route => route.id);
    expect(ids).toEqual(expectedIds);
  };

  test('admin', () => {
    testAppRoutes(JophielRole.Admin, ['account', 'competition', 'training']);
  });

  test('superadmin', () => {
    testAppRoutes(JophielRole.Superadmin, ['account', 'competition', 'training']);
  });

  test('user', () => {
    testAppRoutes(JophielRole.User, ['competition', 'training']);
  });

  test('guest', () => {
    testAppRoutes(JophielRole.Guest, ['competition', 'training']);
  });
});
