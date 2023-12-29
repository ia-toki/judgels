import { Suspense, lazy } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';
import { lazyRetry } from '../../lazy';

const LazyAccountRoutes = lazy(() => lazyRetry(() => import('./account/AccountRoutes')));

function JophielAccountRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <LazyAccountRoutes {...props} />
    </Suspense>
  );
}

export default JophielAccountRoutes;
