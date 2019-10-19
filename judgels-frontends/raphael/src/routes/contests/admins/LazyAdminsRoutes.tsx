import * as React from 'react';

import { LoadingState } from '../../../components/LoadingState/LoadingState';

const AdminsRoutes = React.lazy(() => import('./AdminsRoutes'));

const LazyAdminRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <AdminsRoutes {...props} />
  </React.Suspense>
);

export default LazyAdminRoutes;
