import * as React from 'react';
import { Route, withRouter } from 'react-router';

import ContestDataRoute from './ContestDataRoute';
import SingleContestDataRoute from './single/routes/SingleContestDataRoute';
import SingleContestRoutes from './single/routes/SingleContestRoutes';

const ContestRoutes = () => (
  <div>
    <Route path="/competition/contests" component={ContestDataRoute} />
    <Route path="/competition/contests/:contestId" component={SingleContestDataRoute} />
    <Route path="/competition/contests/:contestId" component={SingleContestRoutes} />
  </div>
);

export default withRouter<any>(ContestRoutes);
