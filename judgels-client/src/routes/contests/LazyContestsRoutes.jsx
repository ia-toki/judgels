import { Suspense, lazy } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';
import { lazyRetry } from '../../lazy';

export const ContestsRoutesPromise = () => lazyRetry(() => import('./MainContestsRoutes'));

const ContestsRoutes = lazy(ContestsRoutesPromise);

function LazyContestsRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <ContestsRoutes {...props} />
    </Suspense>
  );
}

export default LazyContestsRoutes;
