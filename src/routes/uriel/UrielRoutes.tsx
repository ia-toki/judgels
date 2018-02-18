import * as React from 'react';
import * as Loadable from 'react-loadable';

import { LoadingPage } from '../../components/LoadingPage/LoadingPage';

const LoadableCompetitionRoutes = Loadable({
  loader: () => import('./competition/routes/CompetitionRoutes'),
  loading: LoadingPage,
});

export default class UrielRoutes extends React.Component {
  render() {
    return <LoadableCompetitionRoutes />;
  }
}
