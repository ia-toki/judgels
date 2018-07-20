import * as React from 'react';
import * as Loadable from 'react-loadable';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const LoadableAccountsRoutes = Loadable({
  loader: () => import('./accounts/routes/AccountsRoutes'),
  loading: () => <LoadingState large />,
});

export default class JophielAccountsRoutes extends React.PureComponent {
  render() {
    return <LoadableAccountsRoutes />;
  }
}
