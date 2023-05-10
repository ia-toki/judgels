import { lazy, Suspense } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const CoursesRoutes = lazy(() => import('./MainCoursesRoutes'));

function LazyCoursesRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <CoursesRoutes {...props} />
    </Suspense>
  );
}

export default LazyCoursesRoutes;
