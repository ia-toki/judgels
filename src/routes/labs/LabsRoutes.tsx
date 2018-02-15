import * as React from 'react';
import { Route, Switch, withRouter } from 'react-router';

import { Example } from './routes/example/Example/Example';
import ScoreboardContainer from './routes/scoreboard/ScoreboardContainer/ScoreboardContainer';

const LabsRoutes = () => (
  <div>
    <Switch>
      <Route exact path="/labs/example" component={Example} />
      <Route exact path="/labs/scoreboard" component={ScoreboardContainer} />
    </Switch>
  </div>
);

export default withRouter<any>(LabsRoutes);
