import { lazy, Suspense } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const SystemRoutes = lazy(() => import('./SystemRoutes'));

function LazySystemRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <SystemRoutes {...props} />
    </Suspense>
  );
}

export default LazySystemRoutes;
