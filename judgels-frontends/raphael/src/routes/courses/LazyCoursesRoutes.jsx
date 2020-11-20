import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const CoursesRoutes = React.lazy(() => import('./MainCoursesRoutes'));

function LazyCoursesRoutes(props) {
  return (
    <React.Suspense fallback={<LoadingState large />}>
      <CoursesRoutes {...props} />
    </React.Suspense>
  );
}

export default LazyCoursesRoutes;
