import { lazy, Suspense } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const RankingRoutes = lazy(() => import('./RankingRoutes'));

function LazyRankingRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <RankingRoutes {...props} />
    </Suspense>
  );
}

export default LazyRankingRoutes;
