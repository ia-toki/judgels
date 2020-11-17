import * as React from 'react';
import { Route, withRouter } from 'react-router';

import SingleProblemSetProblemRoutes from './SingleProblemSetProblemRoutes';
import SingleProblemSetProblemDataRoute from './SingleProblemSetProblemDataRoute';

const MainSingleProblemSetProblemRoutes = () => (
  <div>
    <Route path="/problems/:problemSetSlug/:problemAlias" component={SingleProblemSetProblemDataRoute} />
    <Route path="/problems/:problemSetSlug/:problemAlias" component={SingleProblemSetProblemRoutes} />
  </div>
);

export default withRouter<any, any>(MainSingleProblemSetProblemRoutes);
