import { createRoute } from '@tanstack/react-router';

import HomePage from './HomePage/HomePage';

export const createHomeRoutes = appRoute => {
  const homeRoute = createRoute({
    getParentRoute: () => appRoute,
    path: '/',
    component: HomePage,
  });

  return homeRoute;
};
