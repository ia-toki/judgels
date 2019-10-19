import * as React from 'react';

import { LoadingState } from '../../../components/LoadingState/LoadingState';

const LazyAdminRoutes = React.lazy(() => import('./AdminsRoutes'));

const MainAdminRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <LazyAdminRoutes {...props} />
  </React.Suspense>
);

export default MainAdminRoutes;
