import * as React from 'react';
import * as Loadable from 'react-loadable';

import { LoadingState } from 'components/LoadingState/LoadingState';

const LoadableTrainingRoutes = Loadable({
  loader: () => import('./training/TrainingRoutes'),
  loading: () => <LoadingState large />,
});

export default class JerahmeelRoutes extends React.PureComponent {
  render() {
    return <LoadableTrainingRoutes />;
  }
}
