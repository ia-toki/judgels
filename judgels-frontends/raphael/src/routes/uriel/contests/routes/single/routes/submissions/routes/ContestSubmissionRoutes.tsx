import * as React from 'react';
import { Route, withRouter } from 'react-router';

import ContestSubmissionsPage from '../ContestSubmissionsPage/ContestSubmissionsPage';
import ContestSubmissionPage from './single/ContestSubmissionPage/ContestSubmissionPage';

const ContestSubmissionRoutes = () => (
  <div>
    <Route exact path="/contests/:contestId/submissions" component={ContestSubmissionsPage} />
    <Route path="/contests/:contestId/submissions/:submissionId" component={ContestSubmissionPage} />
  </div>
);

export default withRouter<any>(ContestSubmissionRoutes);
