import * as React from 'react';
import { Route, Switch } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import ProblemsRoutes from './ProblemsRoutes';

const MainProblemsRoutes = () => (
  <div>
    <Switch>
      <Route path="/problems" component={ProblemsRoutes} />
    </Switch>
  </div>
);

export default withBreadcrumb('Problems')(MainProblemsRoutes);
