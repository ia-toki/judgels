import { Suspense, lazy } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';
import { lazyRetry } from '../../lazy';

const RankingRoutes = lazy(() => lazyRetry(() => import('./RankingRoutes')));

function LazyRankingRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <RankingRoutes {...props} />
    </Suspense>
  );
}

export default LazyRankingRoutes;
