import * as React from 'react';
import { Route, Switch, withRouter } from 'react-router';

import { Example } from './routes/example/Example/Example';

const LabsContainer = () => (
  <div>
    <Switch>
      <Route exact path="/labs/example" component={Example} />
    </Switch>
  </div>
);

export default withRouter<any>(LabsContainer);
