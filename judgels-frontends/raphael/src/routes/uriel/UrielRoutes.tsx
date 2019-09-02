import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

export const UrielRoutesPromise = () => import('./MainContestRoutes');

const LazyUrielRoutes = React.lazy(UrielRoutesPromise);

const UrielRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <LazyUrielRoutes {...props} />
  </React.Suspense>
);

export default UrielRoutes;
