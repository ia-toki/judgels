import { lazy, Suspense } from 'react';

import { lazyRetry } from '../../lazy';
import { LoadingState } from '../../components/LoadingState/LoadingState';

const ProblemsRoutes = lazy(() => lazyRetry(() => import('./MainProblemsRoutes')));

function LazyProblemsRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <ProblemsRoutes {...props} />
    </Suspense>
  );
}

export default LazyProblemsRoutes;
