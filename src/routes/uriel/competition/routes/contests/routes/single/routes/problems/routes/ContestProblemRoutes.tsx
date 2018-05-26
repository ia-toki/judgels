import * as React from 'react';
import { Redirect, Route, Switch, withRouter } from 'react-router';

import ContestProblemsPage from '../ContestProblemsPage/ContestProblemsPage';
import ContestProblemPage from '../routes/single/ContestProblemPage/ContestProblemPage';

const RedirectToTrailingSlash = props => <Redirect to={`${props.location.pathname}/`} />;

const ContestProblemRoutes = () => (
  <div>
    <Switch>
      <Route exact path="/competition/contests/:contestId/problems" component={ContestProblemsPage} />
      <Route
        exact
        strict
        path="/competition/contests/:contestId/problems/:problemAlias"
        render={RedirectToTrailingSlash}
      />
      <Route strict path="/competition/contests/:contestId/problems/:problemAlias/" component={ContestProblemPage} />
    </Switch>
  </div>
);

export default withRouter<any>(ContestProblemRoutes);
