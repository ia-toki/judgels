import * as React from 'react';
import * as Loadable from 'react-loadable';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const LoadableAccountRoutes = Loadable({
  loader: () => import('./accounts/AccountRoutes'),
  loading: () => <LoadingState large />,
});

export default class SuperadminRoutes extends React.PureComponent {
  render() {
    return <LoadableAccountRoutes />;
  }
}
