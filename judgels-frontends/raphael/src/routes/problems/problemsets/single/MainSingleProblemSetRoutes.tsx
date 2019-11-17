import * as React from 'react';
import { Route } from 'react-router';

import SingleProblemSetDataRoute from './SingleProblemSetDataRoute';
import SingleProblemSetRoutes from './SingleProblemSetRoutes';

const MainSingleProblemSetRoutes = () => (
  <div>
    <Route path="/problems/:problemSetSlug" component={SingleProblemSetDataRoute} />
    <Route path="/problems/:problemSetSlug" component={SingleProblemSetRoutes} />
  </div>
);

export default MainSingleProblemSetRoutes;
