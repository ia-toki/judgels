import * as React from 'react';
import * as Loadable from 'react-loadable';

import { LoadingState } from 'components/LoadingState/LoadingState';

export const LoadableUrielRoutes = Loadable({
  loader: () => import('./MainContestRoutes'),
  loading: () => <LoadingState large />,
});

export default class UrielRoutes extends React.PureComponent {
  render() {
    return <LoadableUrielRoutes />;
  }
}
