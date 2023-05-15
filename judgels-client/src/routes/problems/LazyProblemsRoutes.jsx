import { lazy, Suspense } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const ProblemsRoutes = lazy(() => import('./MainProblemsRoutes'));

function LazyProblemsRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <ProblemsRoutes {...props} />
    </Suspense>
  );
}

export default LazyProblemsRoutes;
