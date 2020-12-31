import { Route, Switch, withRouter } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import MainSingleContestRoutes from './contests/single/MainSingleContestRoutes';
import ContestsRoutes from './ContestsRoutes';

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

export default withRouter(withBreadcrumb('Contests')(MainContestRoutes));
