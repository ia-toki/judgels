import { lazy, Suspense } from 'react';

import { lazyRetry } from '../../lazy';
import { LoadingState } from '../../components/LoadingState/LoadingState';

const SubmissionsRoutes = lazy(() => lazyRetry(() => import('./SubmissionsRoutes')));

function LazySubmissionsRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <SubmissionsRoutes {...props} />
    </Suspense>
  );
}

export default LazySubmissionsRoutes;
