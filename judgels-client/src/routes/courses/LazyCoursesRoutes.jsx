import { lazy, Suspense } from 'react';

import { lazyRetry } from '../../lazy';
import { LoadingState } from '../../components/LoadingState/LoadingState';

const CoursesRoutes = lazy(() => lazyRetry(() => import('./MainCoursesRoutes')));

function LazyCoursesRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <CoursesRoutes {...props} />
    </Suspense>
  );
}

export default LazyCoursesRoutes;
