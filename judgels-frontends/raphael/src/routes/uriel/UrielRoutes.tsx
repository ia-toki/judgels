import * as React from 'react';
import * as Loadable from 'react-loadable';

import { LoadingState } from 'components/LoadingState/LoadingState';

const LoadableUrielRoutes = Loadable({
  loader: () => import('./UrielMainRoutes'),
  loading: () => <LoadingState large />,
});

export default class UrielRoutes extends React.PureComponent {
  render() {
    return <LoadableUrielRoutes />;
  }
}
