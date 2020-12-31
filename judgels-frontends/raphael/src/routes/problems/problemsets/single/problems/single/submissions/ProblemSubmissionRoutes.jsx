import { Route, Switch } from 'react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ProblemSubmissionsPage from './ProblemSubmissionsPage/ProblemSubmissionsPage';
import ProblemSubmissionPage from './single/ProblemSubmissionPage/ProblemSubmissionPage';

function ProblemSubmissionRoutes() {
  return (
    <div>
      <Switch>
        <Route exact path="/problems/:problemSetSlug/:problemAlias/submissions" component={ProblemSubmissionsPage} />
        <Route
          exact
          path="/problems/:problemSetSlug/:problemAlias/submissions/mine"
          component={ProblemSubmissionsPage}
        />

        <Route
          path="/problems/:problemSetSlug/:problemAlias/submissions/:submissionId"
          component={ProblemSubmissionPage}
        />
      </Switch>
    </div>
  );
}

export default withBreadcrumb('Submissions')(ProblemSubmissionRoutes);
