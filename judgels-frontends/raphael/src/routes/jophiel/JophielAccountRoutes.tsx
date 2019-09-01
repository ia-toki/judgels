import * as React from 'react';
import Loadable from 'react-loadable';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const LoadableAccountRoutes = Loadable({
  loader: () => import('./account/AccountRoutes'),
  loading: () => <LoadingState large />,
});

export default class JophielAccountRoutes extends React.PureComponent {
  render() {
    return <LoadableAccountRoutes />;
  }
}
