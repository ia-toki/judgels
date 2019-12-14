import * as React from 'react';
import { Route, withRouter, Switch } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import SubmissionsPage from './SubmissionsPage/SubmissionsPage';
import SubmissionPage from './single/SubmissionPage/SubmissionPage';
import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

const SubmissionsRoutes = () => (
  <FullPageLayout>
    <Switch>
      <Route exact path="/submissions" component={SubmissionsPage} />
      <Route exact path="/submissions/mine" component={SubmissionsPage} />

      <Route path="/submissions/:submissionId" component={SubmissionPage} />
    </Switch>
  </FullPageLayout>
);

export default withBreadcrumb('Submissions')(withRouter<any, any>(SubmissionsRoutes));
