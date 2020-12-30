import { lazy, Suspense } from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const LazyProfilesRoutes = lazy(() => import('./profiles/ProfilesRoutes'));

function JophielProfilesRoutes(props) {
  return (
    <Suspense fallback={<LoadingState large />}>
      <LazyProfilesRoutes {...props} />
    </Suspense>
  );
}

export default JophielProfilesRoutes;
