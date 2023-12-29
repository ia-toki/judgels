import { Suspense, lazy } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';
import { lazyRetry } from '../../lazy';

const LazyProfilesRoutes = lazy(() => lazyRetry(() => import('./profiles/ProfilesRoutes')));

function JophielProfilesRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <LazyProfilesRoutes {...props} />
    </Suspense>
  );
}

export default JophielProfilesRoutes;
