import * as React from 'react';
import * as Loadable from 'react-loadable';

import { LoadingState } from 'components/LoadingState/LoadingState';

const LoadableAccountsRoutes = Loadable({
  loader: () => import('./accounts/AccountsRoutes'),
  loading: () => <LoadingState large />,
});

export default class JophielAccountsRoutes extends React.PureComponent {
  render() {
    return <LoadableAccountsRoutes />;
  }
}
