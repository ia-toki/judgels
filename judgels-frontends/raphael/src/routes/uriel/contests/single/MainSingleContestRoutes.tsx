import * as React from 'react';
import { Route, withRouter } from 'react-router';

import SingleContestRoutes from './SingleContestRoutes';
import SingleContestDataRoute from './SingleContestDataRoute';

const MainSingleContestRoutes = () => (
  <div>
    <Route path="/contests/:contestSlug" component={SingleContestDataRoute} />
    <Route path="/contests/:contestSlug" component={SingleContestRoutes} />
  </div>
);

export default withRouter<any>(MainSingleContestRoutes);
