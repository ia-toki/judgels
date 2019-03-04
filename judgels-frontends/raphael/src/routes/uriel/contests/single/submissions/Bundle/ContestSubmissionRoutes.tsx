import * as React from 'react';
import { withRouter, Route } from 'react-router';
import ContestSubmissionsPage from './ContestSubmissionsPage/ContestSubmissionsPage';
import SubmissionSummaryPage from './SubmissionSummaryPage/SubmissionSummaryPage';

const ContestSubmissionRoutes = () => (
  <div>
    <Route path="/contests/:contestSlug/submissions/users/:userId" component={SubmissionSummaryPage} />
    <Route exact path="/contests/:contestSlug/submissions" component={ContestSubmissionsPage} />
  </div>
);

export default withRouter<any>(ContestSubmissionRoutes);
