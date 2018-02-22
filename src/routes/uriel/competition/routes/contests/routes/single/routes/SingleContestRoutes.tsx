import * as React from 'react';
import { Route, Switch, withRouter } from 'react-router';

import SingleContestDataRoute from './SingleContestDataRoute';
import ContestScoreboardPage from './scoreboard/ContestScoreboardPage/ContestScoreboardPage';

const SingleContestRoutes = () => (
  <div>
    <Route path="/competition/contests/:contestJid" component={SingleContestDataRoute} />
    <Switch>
      <Route exact path="/competition/contests/:contestJid/scoreboard" component={ContestScoreboardPage} />
    </Switch>
  </div>
);

export default withRouter<any>(SingleContestRoutes);
