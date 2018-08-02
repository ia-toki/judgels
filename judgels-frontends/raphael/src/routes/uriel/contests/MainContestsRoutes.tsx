import * as React from 'react';
import { Route, withRouter } from 'react-router';

import { withBreadcrumb } from 'components/BreadcrumbWrapper/BreadcrumbWrapper';

import ContestsRoutes from './ContestsRoutes';
import SingleContestRoutes from './single/SingleContestRoutes';
import SingleContestDataRoute from './single/SingleContestDataRoute';

const MainContestRoutes = () => (
  <div>
    <Route exact path="/contests" component={ContestsRoutes} />
    <Route path="/contests/:contestSlug" component={SingleContestDataRoute} />
    <Route path="/contests/:contestSlug" component={SingleContestRoutes} />
  </div>
);

export default withRouter<any>(withBreadcrumb('Contests')(MainContestRoutes));
