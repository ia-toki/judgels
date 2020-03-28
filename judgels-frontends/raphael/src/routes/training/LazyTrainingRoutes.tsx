import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const TrainingRoutes = React.lazy(() => import('./TrainingRoutes'));

const LazyTrainingRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <TrainingRoutes {...props} />
  </React.Suspense>
);

export default LazyTrainingRoutes;
