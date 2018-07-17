import * as React from 'react';
import { Route, Switch } from 'react-router';

import MainTrainingRoutes from './MainTrainingRoutes';
import { withBreadcrumb } from '../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';

const TrainingRoutes = () => (
  <div>
    <Switch>
      <Route path="/training" component={MainTrainingRoutes} />
    </Switch>
  </div>
);

export default withBreadcrumb('Training')(TrainingRoutes);
