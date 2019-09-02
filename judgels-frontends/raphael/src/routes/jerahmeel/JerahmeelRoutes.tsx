import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const LazyTrainingRoutes = React.lazy(() => import('./training/TrainingRoutes'));

const JerahmeelRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <LazyTrainingRoutes {...props} />
  </React.Suspense>
);

export default JerahmeelRoutes;
