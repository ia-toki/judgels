import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const LazyProfilesRoutes = React.lazy(() => import('./profiles/ProfilesRoutes'));

const JophielProfilesRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <LazyProfilesRoutes {...props} />
  </React.Suspense>
);

export default JophielProfilesRoutes;
