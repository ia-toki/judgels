import { Suspense, lazy } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';
import { lazyRetry } from '../../lazy';

const SystemRoutes = lazy(() => lazyRetry(() => import('./SystemRoutes')));

function LazySystemRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <SystemRoutes {...props} />
    </Suspense>
  );
}

export default LazySystemRoutes;
