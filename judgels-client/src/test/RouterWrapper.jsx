import {
  Outlet,
  RouterProvider,
  createMemoryHistory,
  createRootRoute,
  createRoute,
  createRouter,
} from '@tanstack/react-router';
import { parse, stringify } from 'query-string';

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
      component: () => children,
    });
    routeTree = rootRoute.addChildren([childRoute]);
  } else {
    // Simple case: just render children at root
    routeTree = createRootRoute({
      component: () => children,
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
