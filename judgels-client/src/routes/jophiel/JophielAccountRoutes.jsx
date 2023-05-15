import { lazy, Suspense } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const LazyAccountRoutes = lazy(() => import('./account/AccountRoutes'));

function JophielAccountRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <LazyAccountRoutes {...props} />
    </Suspense>
  );
}

export default JophielAccountRoutes;
