import * as React from 'react';
import { Route, Switch } from 'react-router';

import { withBreadcrumb } from '../../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import MainTrainingRoutes from './MainTrainingRoutes';

const TrainingRoutes = () => (
  <div>
    <Switch>
      <Route path="/training" component={MainTrainingRoutes} />
    </Switch>
  </div>
);

export default withBreadcrumb('Training')(TrainingRoutes);
