import { Suspense, lazy } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';
import { lazyRetry } from '../../lazy';

const TrainingRoutes = lazy(() => lazyRetry(() => import('./TrainingRoutes')));

function LazyTrainingRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <TrainingRoutes {...props} />
    </Suspense>
  );
}

export default LazyTrainingRoutes;
