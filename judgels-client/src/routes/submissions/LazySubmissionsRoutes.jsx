import { Suspense, lazy } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';
import { lazyRetry } from '../../lazy';

const SubmissionsRoutes = lazy(() => lazyRetry(() => import('./SubmissionsRoutes')));

function LazySubmissionsRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <SubmissionsRoutes {...props} />
    </Suspense>
  );
}

export default LazySubmissionsRoutes;
