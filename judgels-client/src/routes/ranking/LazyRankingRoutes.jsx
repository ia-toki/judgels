import { lazy, Suspense } from 'react';

import { lazyRetry } from '../../lazy';
import { LoadingState } from '../../components/LoadingState/LoadingState';

const RankingRoutes = lazy(() => lazyRetry(() => import('./RankingRoutes')));

function LazyRankingRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <RankingRoutes {...props} />
    </Suspense>
  );
}

export default LazyRankingRoutes;
