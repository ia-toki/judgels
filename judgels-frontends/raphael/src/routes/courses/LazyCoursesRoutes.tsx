import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const CoursesRoutes = React.lazy(() => import('./MainCoursesRoutes'));

const LazyCoursesRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <CoursesRoutes {...props} />
  </React.Suspense>
);

export default LazyCoursesRoutes;
