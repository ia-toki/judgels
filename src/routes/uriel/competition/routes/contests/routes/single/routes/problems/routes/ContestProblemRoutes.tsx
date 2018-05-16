import * as React from 'react';
import { Route, withRouter } from 'react-router';

import ContestProblemsPage from '../ContestProblemsPage/ContestProblemsPage';
import ContestProblemPage from '../routes/single/ContestProblemPage/ContestProblemPage';

const ContestProblemRoutes = () => (
  <div>
    <Route exact path="/competition/contests/:contestId/problems" component={ContestProblemsPage} />
    <Route path="/competition/contests/:contestId/problems/:problemAlias" component={ContestProblemPage} />
  </div>
);

export default withRouter<any>(ContestProblemRoutes);
