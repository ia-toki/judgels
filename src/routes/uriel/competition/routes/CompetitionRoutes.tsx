import * as React from 'react';
import { Route, Switch } from 'react-router';

import ContestRoutes from './contests/routes/ContestRoutes';
import MainCompetitionRoutes from './MainCompetitionRoutes';
import { withBreadcrumb } from '../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';

const CompetitionRoutes = () => (
  <div>
    <Switch>
      <Route path="/beta/competition/contests/:contestJid" component={ContestRoutes} />
      <Route path="/beta/competition" component={MainCompetitionRoutes} />
    </Switch>
  </div>
);

export default withBreadcrumb('Competition')(CompetitionRoutes);
