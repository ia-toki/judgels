import * as React from 'react';
import { Route, Switch, withRouter } from 'react-router';

import ContestScoreboardPage from './scoreboard/ContestScoreboardPage/ContestScoreboardPage';

const SingleContestRoutes = () => (
  <div>
    <Switch>
      <Route exact path="/competition/contests/:contestJid/scoreboard" component={ContestScoreboardPage} />
    </Switch>
  </div>
);

export default withRouter<any>(SingleContestRoutes);
