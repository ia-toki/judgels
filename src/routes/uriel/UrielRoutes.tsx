import * as React from 'react';
import * as Loadable from 'react-loadable';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const LoadableCompetitionRoutes = Loadable({
  loader: () => import('./competition/routes/CompetitionRoutes'),
  loading: () => <LoadingState large />,
});

export default class UrielRoutes extends React.Component {
  render() {
    return <LoadableCompetitionRoutes />;
  }
}
