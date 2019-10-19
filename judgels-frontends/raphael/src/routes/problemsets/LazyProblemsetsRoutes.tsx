import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const ProblemsetsRoutes = React.lazy(() => import('./MainProblemsetsRoutes'));

const LazyProblemsetsRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <ProblemsetsRoutes {...props} />
  </React.Suspense>
);

export default LazyProblemsetsRoutes;
