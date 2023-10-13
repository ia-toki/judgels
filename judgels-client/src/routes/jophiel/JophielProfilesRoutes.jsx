import { lazy, Suspense } from 'react';

import { lazyRetry } from '../../lazy';
import { LoadingState } from '../../components/LoadingState/LoadingState';

const LazyProfilesRoutes = lazy(() => lazyRetry(() => import('./profiles/ProfilesRoutes')));

function JophielProfilesRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <LazyProfilesRoutes {...props} />
    </Suspense>
  );
}

export default JophielProfilesRoutes;
