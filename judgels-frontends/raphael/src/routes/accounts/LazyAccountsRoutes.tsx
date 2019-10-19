import * as React from 'react';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const AccountsRoutes = React.lazy(() => import('./AccountsRoutes'));

const LazyAccountsRoutes = props => (
  <React.Suspense fallback={<LoadingState large />}>
    <AccountsRoutes {...props} />
  </React.Suspense>
);

export default LazyAccountsRoutes;
