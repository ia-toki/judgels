import { lazy, Suspense } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

export const ContestsRoutesPromise = () => import('./MainContestsRoutes');

const ContestsRoutes = lazy(ContestsRoutesPromise);

function LazyContestsRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <ContestsRoutes {...props} />
    </Suspense>
  );
}

export default LazyContestsRoutes;
