import { Navigate, createRoute, lazyRouteComponent } from '@tanstack/react-router';

import { isTLX } from '../../conf';
import { retryImport } from '../../lazy';
import { createDocumentTitle } from '../../utils/title';

export const createAdminRoutes = appRoute => {
  if (!isTLX()) {
    return null;
  }

  const adminRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'admin',
    component: lazyRouteComponent(retryImport(() => import('./AdminLayout'))),
    head: () => ({ meta: [{ title: createDocumentTitle('Admin') }] }),
  });

  const adminIndexRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: '/',
    component: () => <Navigate to="/admin/users" />,
  });

  const adminUsersRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'users',
    component: lazyRouteComponent(retryImport(() => import('./users/UsersPage/UsersPage'))),
  });

  const adminUserViewRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'users/$userJid',
    component: lazyRouteComponent(retryImport(() => import('./users/UserViewPage/UserViewPage'))),
  });

  const adminRolesRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'roles',
    component: lazyRouteComponent(retryImport(() => import('./roles/RolesPage/RolesPage'))),
  });

  return adminRoute.addChildren([adminIndexRoute, adminUsersRoute, adminUserViewRoute, adminRolesRoute]);
};
