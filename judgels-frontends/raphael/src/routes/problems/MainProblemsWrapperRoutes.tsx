import * as React from 'react';
import { Route } from 'react-router';

import ProblemsRoutes from './ProblemsRoutes';

const MainProblemsWrapperRoutes = () => (
  <div>
    <Route path="/problems" component={ProblemsRoutes} />
  </div>
);

export default MainProblemsWrapperRoutes;
