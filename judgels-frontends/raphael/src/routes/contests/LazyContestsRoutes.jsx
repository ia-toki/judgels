import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

export const ContestsRoutesPromise = () => import('./MainContestsRoutes');

const ContestsRoutes = React.lazy(ContestsRoutesPromise);

const LazyContestsRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <ContestsRoutes {...props} />
  </React.Suspense>
);

export default LazyContestsRoutes;
