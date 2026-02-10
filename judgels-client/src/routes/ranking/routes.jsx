import { createRoute, lazyRouteComponent } from '@tanstack/react-router';

import { retryImport } from '../../lazy';
import { createDocumentTitle } from '../../utils/title';

export const createRankingRoutes = appRoute => {
  const rankingRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'ranking',
    component: lazyRouteComponent(retryImport(() => import('./RankingLayout'))),
    head: () => ({ meta: [{ title: createDocumentTitle('Ranking') }] }),
  });

  const rankingIndexRoute = createRoute({
    getParentRoute: () => rankingRoute,
    path: '/',
    component: lazyRouteComponent(retryImport(() => import('./ratings/RatingsPage/RatingsPage'))),
  });

  const rankingRatingSystemRoute = createRoute({
    getParentRoute: () => rankingRoute,
    path: 'rating-system',
    component: lazyRouteComponent(retryImport(() => import('./ratings/RatingSystemPage/RatingSystemPage'))),
    head: () => ({ meta: [{ title: createDocumentTitle('Rating system') }] }),
  });

  return rankingRoute.addChildren([rankingIndexRoute, rankingRatingSystemRoute]);
};
