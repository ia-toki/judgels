import { Route, withRouter, Switch } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import SubmissionsPage from './SubmissionsPage/SubmissionsPage';
import SubmissionPage from './single/SubmissionPage/SubmissionPage';
import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

function SubmissionsRoutes() {
  return (
    <FullPageLayout>
      <Switch>
        <Route exact path="/submissions" component={SubmissionsPage} />
        <Route exact path="/submissions/mine" component={SubmissionsPage} />

        <Route path="/submissions/:submissionId" component={SubmissionPage} />
      </Switch>
    </FullPageLayout>
  );
}

export default withBreadcrumb('Submissions')(withRouter(SubmissionsRoutes));
