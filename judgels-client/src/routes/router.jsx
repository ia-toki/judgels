import { createRootRoute, createRoute, createRouter } from '@tanstack/react-router';
import { parse, stringify } from 'query-string';

import { LoadingState } from '../components/LoadingState/LoadingState';
import { NotFoundPage } from '../components/NotFoundPage/NotFoundPage';
import { APP_CONFIG } from '../conf';
import { NotFoundError } from '../modules/api/error';
import { userWebConfigQueryOptions } from '../modules/queries/userWeb';
import { queryClient } from '../modules/queryClient';
import App from './App';
import Root from './Root';
import { createAccountRoutes } from './account/routes';
import { createAdminRoutes } from './admin/routes';
import { createContestsRoutes } from './contests/routes';
import { createCoursesRoutes } from './courses/routes';
import { createHomeRoutes } from './home/routes';
import { createProblemsRoutes } from './problems/routes';
import { createRankingRoutes } from './ranking/routes';
import { createSubmissionsRoutes } from './submissions/routes';

const rootRoute = createRootRoute({
  component: Root,
});

export const appRoute = createRoute({
  getParentRoute: () => rootRoute,
  id: 'app',
  component: App,
  loader: async () => {
    await queryClient.ensureQueryData(userWebConfigQueryOptions());
  },
});

const appChildren = [
  ...createHomeRoutes(appRoute),
  ...createAccountRoutes(appRoute),
  createAdminRoutes(appRoute),
  createContestsRoutes(appRoute),
  createCoursesRoutes(appRoute),
  createProblemsRoutes(appRoute),
  createSubmissionsRoutes(appRoute),
  createRankingRoutes(appRoute),
].filter(Boolean);

const routeTree = rootRoute.addChildren([appRoute.addChildren(appChildren)]);

function DefaultErrorComponent({ error }) {
  if (error instanceof NotFoundError) {
    return <NotFoundPage />;
  }
  return (
    <div style={{ padding: 20, textAlign: 'center' }}>
      <p>Something went wrong.</p>
      <button onClick={() => window.location.reload()}>Refresh</button>
    </div>
  );
}

export const router = createRouter({
  routeTree,
  defaultPreload: 'intent',
  scrollRestoration: true,
  defaultPendingComponent: () => <LoadingState large />,
  defaultErrorComponent: DefaultErrorComponent,
  defaultNotFoundComponent: NotFoundPage,
  parseSearch: searchStr => parse(searchStr),
  stringifySearch: searchObj => {
    const str = stringify(searchObj);
    return str ? `?${str}` : '';
  },
});
