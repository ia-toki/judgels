import { Route } from 'react-router';

import ContestSubmissionsPage from './ContestSubmissionsPage/ContestSubmissionsPage';
import ContestSubmissionPage from './single/ContestSubmissionPage/ContestSubmissionPage';

export default function ContestSubmissionRoutes() {
  return (
    <div>
      <Route exact path="/contests/:contestSlug/submissions" component={ContestSubmissionsPage} />
      <Route path="/contests/:contestSlug/submissions/:submissionId" component={ContestSubmissionPage} />
    </div>
  );
}
