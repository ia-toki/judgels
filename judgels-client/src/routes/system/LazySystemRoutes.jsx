import { lazy, Suspense } from 'react';

import { lazyRetry } from '../../lazy';
import { LoadingState } from '../../components/LoadingState/LoadingState';

const SystemRoutes = lazy(() => lazyRetry(() => import('./SystemRoutes')));

function LazySystemRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <SystemRoutes {...props} />
    </Suspense>
  );
}

export default LazySystemRoutes;
