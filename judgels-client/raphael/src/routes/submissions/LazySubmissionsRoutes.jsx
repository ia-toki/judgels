import { lazy, Suspense } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const SubmissionsRoutes = lazy(() => import('./SubmissionsRoutes'));

function LazySubmissionsRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <SubmissionsRoutes {...props} />
    </Suspense>
  );
}

export default LazySubmissionsRoutes;
