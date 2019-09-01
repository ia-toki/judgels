import * as React from 'react';
import Loadable from 'react-loadable';

import { LoadingState } from '../../../components/LoadingState/LoadingState';

const LoadableAdminsRoutes = Loadable({
  loader: () => import('./AdminsRoutes'),
  loading: () => <LoadingState large />,
});

export default class MainAdminsRoutes extends React.PureComponent {
  render() {
    return <LoadableAdminsRoutes />;
  }
}
