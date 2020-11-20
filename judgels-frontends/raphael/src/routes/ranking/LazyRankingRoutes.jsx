import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const RankingRoutes = React.lazy(() => import('./RankingRoutes'));

function LazyRankingRoutes(props) {
  return (
    <React.Suspense fallback={<LoadingState large />}>
      <RankingRoutes {...props} />
    </React.Suspense>
  );
}

export default LazyRankingRoutes;
