import { Route, Switch } from 'react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ProblemSubmissionSummaryPage from './ProblemSubmissionSummaryPage/ProblemSubmissionSummaryPage';
import ProblemSubmissionsPage from './ProblemSubmissionsPage/ProblemSubmissionsPage';

function ProblemItemSubmissionRoutes() {
  return (
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
}

export default withBreadcrumb('Results')(ProblemItemSubmissionRoutes);
