import { Suspense, lazy } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';
import { lazyRetry } from '../../lazy';

const CoursesRoutes = lazy(() => lazyRetry(() => import('./MainCoursesRoutes')));

function LazyCoursesRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <CoursesRoutes {...props} />
    </Suspense>
  );
}

export default LazyCoursesRoutes;
