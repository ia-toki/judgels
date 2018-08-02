import * as React from 'react';
import * as Loadable from 'react-loadable';

import { LoadingState } from 'components/LoadingState/LoadingState';

const LoadableContestsRoutes = Loadable({
  loader: () => import('./contests/MainContestsRoutes'),
  loading: () => <LoadingState large />,
});

export default class UrielContestsRoutes extends React.PureComponent {
  render() {
    return <LoadableContestsRoutes />;
  }
}
