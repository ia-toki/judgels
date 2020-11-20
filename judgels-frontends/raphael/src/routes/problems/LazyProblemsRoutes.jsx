import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const ProblemsRoutes = React.lazy(() => import('./MainProblemsRoutes'));

function LazyProblemsRoutes(props) {
  return (
    <React.Suspense fallback={<LoadingState large />}>
      <ProblemsRoutes {...props} />
    </React.Suspense>
  );
}

export default LazyProblemsRoutes;
