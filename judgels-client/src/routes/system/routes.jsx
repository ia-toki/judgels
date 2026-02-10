import { Navigate, createRoute, lazyRouteComponent } from '@tanstack/react-router';

import { isTLX } from '../../conf';
import { retryImport } from '../../lazy';
import { createDocumentTitle } from '../../utils/title';

export const createSystemRoutes = appRoute => {
  if (!isTLX()) {
    return null;
  }

  const systemRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'system',
    component: lazyRouteComponent(retryImport(() => import('./SystemLayout'))),
    head: () => ({ meta: [{ title: createDocumentTitle('System') }] }),
  });

  const systemIndexRoute = createRoute({
    getParentRoute: () => systemRoute,
    path: '/',
    component: () => <Navigate to="/system/ratings" />,
  });

  const systemRatingsRoute = createRoute({
    getParentRoute: () => systemRoute,
    path: 'ratings',
    component: lazyRouteComponent(retryImport(() => import('./ratings/RatingsPage/RatingsPage'))),
  });

  return systemRoute.addChildren([systemIndexRoute, systemRatingsRoute]);
};
