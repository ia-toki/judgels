import * as React from 'react';
import { Route, withRouter } from 'react-router';

import { withBreadcrumb } from '../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContestsRoutes from './routes/ContestsRoutes';
import SingleContestRoutes from './routes/single/routes/SingleContestRoutes';
import SingleContestDataRoute from './routes/single/routes/SingleContestDataRoute';

const MainContestRoutes = () => (
  <div>
    <Route exact path="/contests" component={ContestsRoutes} />
    <Route path="/contests/:contestSlug" component={SingleContestDataRoute} />
    <Route path="/contests/:contestSlug" component={SingleContestRoutes} />
  </div>
);

export default withRouter<any>(withBreadcrumb('Contests')(MainContestRoutes));
