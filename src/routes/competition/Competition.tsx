import * as React from 'react';
import * as Loadable from 'react-loadable';

import { LoadingPage } from '../../components/LoadingPage/LoadingPage';

const LoadableComponent = Loadable({
  loader: () => import('./CompetitionPage'),
  loading: LoadingPage,
});

export default class Competition extends React.Component {
  render() {
    return <LoadableComponent />;
  }
}
