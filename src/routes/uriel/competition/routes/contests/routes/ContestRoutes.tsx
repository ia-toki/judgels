import * as React from 'react';
import { Route, withRouter } from 'react-router';

import ContestDataRoute from './ContestDataRoute';
import SingleContestDataRoute from './single/routes/SingleContestDataRoute';
import SingleContestRoutes from './single/routes/SingleContestRoutes';

const ContestRoutes = () => (
  <div>
    <Route path="/beta/competition/contests" component={ContestDataRoute} />
    <Route path="/beta/competition/contests/:contestJid" component={SingleContestDataRoute} />
    <Route path="/beta/competition/contests/:contestJid" component={SingleContestRoutes} />
  </div>
);

export default withRouter<any>(ContestRoutes);
