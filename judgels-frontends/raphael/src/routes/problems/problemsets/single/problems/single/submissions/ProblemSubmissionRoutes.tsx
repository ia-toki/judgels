import * as React from 'react';
import { Route, withRouter, Switch } from 'react-router';

import ProblemSubmissionsPage from './ProblemSubmissionsPage/ProblemSubmissionsPage';
import ProblemSubmissionPage from './single/ProblemSubmissionPage/ProblemSubmissionPage';

const ProblemSubmissionRoutes = () => (
  <div>
    <Switch>
      <Route exact path="/problems/:problemSetSlug/:problemAlias/submissions" component={ProblemSubmissionsPage} />
      <Route exact path="/problems/:problemSetSlug/:problemAlias/submissions/mine" component={ProblemSubmissionsPage} />

      <Route
        path="/problems/:problemSetSlug/:problemAlias/submissions/:submissionId"
        component={ProblemSubmissionPage}
      />
    </Switch>
  </div>
);

export default withRouter<any, any>(ProblemSubmissionRoutes);
