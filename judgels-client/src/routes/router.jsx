import { createRootRoute, createRoute, createRouter } from '@tanstack/react-router';
import { parse, stringify } from 'query-string';

import { LoadingState } from '../components/LoadingState/LoadingState';
import { APP_CONFIG } from '../conf';
import App from './App';
import Root from './Root';
import { createContestsRoutes } from './contests/routes';
import { createCoursesRoutes } from './courses/routes';
import { createJophielRoutes } from './jophiel/routes';
import { createProblemsRoutes } from './problems/routes';
import { createRankingRoutes } from './ranking/routes';
import { createSubmissionsRoutes } from './submissions/routes';
import { createSystemRoutes } from './system/routes';
import { createTrainingRoutes } from './training/routes';

const rootRoute = createRootRoute({
  component: Root,
});

export const appRoute = createRoute({
  getParentRoute: () => rootRoute,
  id: 'app',
  component: App,
});

const appChildren = [
  createSystemRoutes(appRoute),
  createJophielRoutes(appRoute),
  createContestsRoutes(appRoute),
  createCoursesRoutes(appRoute),
  createProblemsRoutes(appRoute),
  createTrainingRoutes(appRoute),
  createSubmissionsRoutes(appRoute),
  createRankingRoutes(appRoute),
].filter(Boolean);

const routeTree = rootRoute.addChildren([appRoute.addChildren(appChildren)]);

export const router = createRouter({
  routeTree,
  defaultPreload: 'intent',
  defaultPendingComponent: () => <LoadingState large />,
  parseSearch: searchStr => parse(searchStr),
  stringifySearch: searchObj => {
    const str = stringify(searchObj);
    return str ? `?${str}` : '';
  },
});
