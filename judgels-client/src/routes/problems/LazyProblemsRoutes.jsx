import { Suspense, lazy } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';
import { lazyRetry } from '../../lazy';

const ProblemsRoutes = lazy(() => lazyRetry(() => import('./MainProblemsRoutes')));

function LazyProblemsRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <ProblemsRoutes {...props} />
    </Suspense>
  );
}

export default LazyProblemsRoutes;
