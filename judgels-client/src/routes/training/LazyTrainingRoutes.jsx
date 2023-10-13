import { lazy, Suspense } from 'react';

import { lazyRetry } from '../../lazy';
import { LoadingState } from '../../components/LoadingState/LoadingState';

const TrainingRoutes = lazy(() => lazyRetry(() => import('./TrainingRoutes')));

function LazyTrainingRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <TrainingRoutes {...props} />
    </Suspense>
  );
}

export default LazyTrainingRoutes;
