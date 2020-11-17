import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const RankingRoutes = React.lazy(() => import('./RankingRoutes'));

const LazyRankingRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <RankingRoutes {...props} />
  </React.Suspense>
);

export default LazyRankingRoutes;
