import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const ProblemsRoutes = React.lazy(() => import('./MainProblemsRoutes'));

const LazyProblemsRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <ProblemsRoutes {...props} />
  </React.Suspense>
);

export default LazyProblemsRoutes;
