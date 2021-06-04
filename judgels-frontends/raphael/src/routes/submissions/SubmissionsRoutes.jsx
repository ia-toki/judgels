import { Route, withRouter, Switch } from 'react-router';

import { Card } from '../../components/Card/Card';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import SubmissionsPage from './SubmissionsPage/SubmissionsPage';
import SubmissionPage from './single/SubmissionPage/SubmissionPage';
import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import './SubmissionsRoutes.scss';

function SubmissionsRoutes() {
  return (
    <FullPageLayout>
      <Card className="submissions-routes" title="Submissions">
        <Switch>
          <Route exact path="/submissions" component={SubmissionsPage} />
          <Route exact path="/submissions/mine" component={SubmissionsPage} />

          <Route path="/submissions/:submissionId" component={SubmissionPage} />
        </Switch>
      </Card>
    </FullPageLayout>
  );
}

export default withBreadcrumb('Submissions')(withRouter(SubmissionsRoutes));
