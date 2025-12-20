import { Route, Switch } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContestsRoutes from './ContestsRoutes';
import MainSingleContestRoutes from './contests/single/MainSingleContestRoutes';

function MainContestRoutes() {
  return (
    <div>
      <Switch>
        <Route path="/contests/:contestSlug([a-zA-Z0-9-]+)" component={MainSingleContestRoutes} />
        <Route path="/contests" component={ContestsRoutes} />
      </Switch>
    </div>
  );
}

export default withBreadcrumb('Contests')(MainContestRoutes);
