import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const SystemRoutes = React.lazy(() => import('./SystemRoutes'));

const LazySystemRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <SystemRoutes {...props} />
  </React.Suspense>
);

export default LazySystemRoutes;
