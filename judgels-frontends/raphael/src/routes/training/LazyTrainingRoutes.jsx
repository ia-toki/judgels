import { lazy, Suspense } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const TrainingRoutes = lazy(() => import('./TrainingRoutes'));

function LazyTrainingRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <TrainingRoutes {...props} />
    </Suspense>
  );
}

export default LazyTrainingRoutes;
