import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const LazyRankingRoutes = React.lazy(() => import('./routes/RankingRoutes'));

const JudgelsRankingRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <LazyRankingRoutes {...props} />
  </React.Suspense>
);

export default JudgelsRankingRoutes;
