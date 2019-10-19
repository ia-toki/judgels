import * as React from 'react';
import { Route, Switch } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import ProblemsetsRoutes from './ProblemsetsRoutes';

const MainProblemsetsRoutes = () => (
  <div>
    <Switch>
      <Route path="/problemsets" component={ProblemsetsRoutes} />
    </Switch>
  </div>
);

export default withBreadcrumb('Problemsets')(MainProblemsetsRoutes);
