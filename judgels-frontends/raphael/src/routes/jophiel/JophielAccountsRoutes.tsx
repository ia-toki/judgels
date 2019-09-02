import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const LazyAccountsRoutes = React.lazy(() => import('./accounts/AccountsRoutes'));

const JophielAccountsRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <LazyAccountsRoutes {...props} />
  </React.Suspense>
);

export default JophielAccountsRoutes;
