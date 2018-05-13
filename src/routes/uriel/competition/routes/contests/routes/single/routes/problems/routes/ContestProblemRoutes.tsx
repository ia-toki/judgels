import * as React from 'react';
import { Route, withRouter } from 'react-router';

import ContestProblemsPage from '../ContestProblemsPage/ContestProblemsPage';

const ContestProblemRoutes = () => (
  <div>
    <Route exact path="/competition/contests/:contestId/problems" component={ContestProblemsPage} />
  </div>
);

export default withRouter<any>(ContestProblemRoutes);
