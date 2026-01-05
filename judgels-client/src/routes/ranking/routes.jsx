import { createRoute, lazyRouteComponent } from '@tanstack/react-router';

import { createDocumentTitle } from '../../utils/title';

export const createRankingRoutes = appRoute => {
  const rankingRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'ranking',
    component: lazyRouteComponent(() => import('./RankingLayout')),
    head: () => ({ meta: [{ title: createDocumentTitle('Ranking') }] }),
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
    head: () => ({ meta: [{ title: createDocumentTitle('Rating system') }] }),
  });

  return rankingRoute.addChildren([rankingIndexRoute, rankingRatingSystemRoute]);
};
