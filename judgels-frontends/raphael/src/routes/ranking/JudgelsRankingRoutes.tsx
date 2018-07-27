import * as React from 'react';
import * as Loadable from 'react-loadable';

import { LoadingState } from '../../components/LoadingState/LoadingState';

const LoadableRankingRoutes = Loadable({
  loader: () => import('./routes/RankingRoutes'),
  loading: () => <LoadingState large />,
});

export default class JudgelsRankingRoutes extends React.PureComponent {
  render() {
    return <LoadableRankingRoutes />;
  }
}
