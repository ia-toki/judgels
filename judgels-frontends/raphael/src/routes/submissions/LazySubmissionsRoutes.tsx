import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const SubmissionsRoutes = React.lazy(() => import('./SubmissionsRoutes'));

const LazySubmissionsRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <SubmissionsRoutes {...props} />
  </React.Suspense>
);

export default LazySubmissionsRoutes;
