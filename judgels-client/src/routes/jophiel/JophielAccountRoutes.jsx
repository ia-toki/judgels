import { lazy, Suspense } from 'react';

import { lazyRetry } from '../../lazy';
import { LoadingState } from '../../components/LoadingState/LoadingState';

const LazyAccountRoutes = lazy(() => lazyRetry(() => import('./account/AccountRoutes')));

function JophielAccountRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <LazyAccountRoutes {...props} />
    </Suspense>
  );
}

export default JophielAccountRoutes;
