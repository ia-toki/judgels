import {
  Outlet,
  RouterProvider,
  createMemoryHistory,
  createRootRoute,
  createRoute,
  createRouter,
} from '@tanstack/react-router';
import { parse, stringify } from 'query-string';
import { Suspense } from 'react';

export function createTestRouter(component, initialEntries = ['/']) {
  const rootRoute = createRootRoute({
    component: component || Outlet,
  });

  const router = createRouter({
    routeTree: rootRoute,
    history: createMemoryHistory({ initialEntries }),
    parseSearch: searchStr => parse(searchStr),
    stringifySearch: searchObj => {
      const str = stringify(searchObj);
      return str ? `?${str}` : '';
    },
    defaultPendingMinMs: 0,
  });

  return router;
}

export function TestRouter({ children, initialEntries = ['/'], path }) {
  let routeTree;
  if (path) {
    // Create a route with the specified path pattern to support useParams
    const rootRoute = createRootRoute({
      component: Outlet,
    });
    const childRoute = createRoute({
      getParentRoute: () => rootRoute,
      path,
      component: () => <Suspense fallback={<div>Loading...</div>}>{children}</Suspense>,
    });
    routeTree = rootRoute.addChildren([childRoute]);
  } else {
    // Simple case: just render children at root
    routeTree = createRootRoute({
      component: () => <Suspense fallback={<div>Loading...</div>}>{children}</Suspense>,
    });
  }

  const router = createRouter({
    routeTree,
    history: createMemoryHistory({ initialEntries }),
    parseSearch: searchStr => parse(searchStr),
    stringifySearch: searchObj => {
      const str = stringify(searchObj);
      return str ? `?${str}` : '';
    },
    defaultPendingMinMs: 0,
  });

  return <RouterProvider router={router} />;
}
