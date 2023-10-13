import { lazy, Suspense } from 'react';

import { lazyRetry } from '../../lazy';
import { LoadingState } from '../../components/LoadingState/LoadingState';

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
