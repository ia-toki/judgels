import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const LazyAccountRoutes = React.lazy(() => import('./account/AccountRoutes'));

function JophielAccountRoutes(props) {
  return (
    <React.Suspense fallback={<LoadingState large />}>
      <LazyAccountRoutes {...props} />
    </React.Suspense>
  );
}

export default JophielAccountRoutes;
