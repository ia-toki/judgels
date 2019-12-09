import * as React from 'react';
import { withRouter, Route, Switch } from 'react-router';

import ProblemSubmissionsPage from './ProblemSubmissionsPage/ProblemSubmissionsPage';
import ProblemSubmissionSummaryPage from './ProblemSubmissionSummaryPage/ProblemSubmissionSummaryPage';

const ProblemItemSubmissionRoutes = () => (
  <div>
    <Switch>
      <Route
        path="/problems/:problemSetSlug/:problemAlias/results/users/:username"
        component={ProblemSubmissionSummaryPage}
      />
      <Route path="/problems/:problemSetSlug/:problemAlias/results/all" component={ProblemSubmissionsPage} />
      <Route path="/problems/:problemSetSlug/:problemAlias/results" component={ProblemSubmissionSummaryPage} />
    </Switch>
  </div>
);

export default withRouter(ProblemItemSubmissionRoutes);
