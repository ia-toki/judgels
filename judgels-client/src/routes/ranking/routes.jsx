import { createRoute, lazyRouteComponent } from '@tanstack/react-router';

export const createRankingRoutes = appRoute => {
  const rankingRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'ranking',
    component: lazyRouteComponent(() => import('./RankingLayout')),
  });

  const rankingIndexRoute = createRoute({
    getParentRoute: () => rankingRoute,
    path: '/',
    component: lazyRouteComponent(() => import('./ratings/RatingsPage/RatingsPage')),
  });

  const rankingRatingSystemRoute = createRoute({
    getParentRoute: () => rankingRoute,
    path: 'rating-system',
    component: lazyRouteComponent(() => import('./ratings/RatingSystemPage/RatingSystemPage')),
  });

  return rankingRoute.addChildren([rankingIndexRoute, rankingRatingSystemRoute]);
};
