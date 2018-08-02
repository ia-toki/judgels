import * as React from 'react';
import * as Loadable from 'react-loadable';

import { LoadingState } from 'components/LoadingState/LoadingState';

const LoadableProfilesRoutes = Loadable({
  loader: () => import('./profiles/ProfilesRoutes'),
  loading: () => <LoadingState large />,
});

export default class JophielProfilesRoutes extends React.PureComponent {
  render() {
    return <LoadableProfilesRoutes />;
  }
}
