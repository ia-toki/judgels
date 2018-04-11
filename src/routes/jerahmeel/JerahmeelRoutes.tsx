import * as React from 'react';
import * as Loadable from 'react-loadable';

import { LoadingPage } from '../../components/LoadingPage/LoadingPage';

const LoadableTrainingRoutes = Loadable({
  loader: () => import('./training/routes/TrainingRoutes'),
  loading: LoadingPage,
});

export default class JerahmeelRoutes extends React.Component {
  render() {
    return <LoadableTrainingRoutes />;
  }
}
