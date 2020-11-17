import * as React from 'react';
import { Route, Switch } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import MainProblemsWrapperRoutes from './MainProblemsWrapperRoutes';
import MainSingleProblemSetRoutes from './problemsets/single/MainSingleProblemSetRoutes';

const MainProblemsRoutes = () => (
  <div>
    <Switch>
      <Route exact path="/problems" component={MainProblemsWrapperRoutes} />
      <Route path="/problems/problemsets" component={MainProblemsWrapperRoutes} />
      <Route path="/problems/submissions" component={MainProblemsWrapperRoutes} />
      <Route path="/problems" component={MainSingleProblemSetRoutes} />
    </Switch>
  </div>
);

export default withBreadcrumb('Problems')(MainProblemsRoutes);
