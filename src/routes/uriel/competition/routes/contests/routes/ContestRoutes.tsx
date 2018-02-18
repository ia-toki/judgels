import * as React from 'react';
import { Route, Switch, withRouter } from 'react-router';

import ContestListPage from './list/ContestListPage/ContestListPage';
import SingleContestRoutes from './single/routes/SingleContestRoutes';

const ContestRoutes = () => (
  <div>
    <Route path="/competition/contests" component={ContestListPage} />
    <Switch>
      <Route path="/competition/contests/:contestJid" component={SingleContestRoutes} />
    </Switch>
  </div>
);

export default withRouter<any>(ContestRoutes);
