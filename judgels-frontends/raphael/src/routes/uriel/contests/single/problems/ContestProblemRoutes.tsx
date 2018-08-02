import * as React from 'react';
import { Route, withRouter } from 'react-router';

import ContestProblemsPage from './ContestProblemsPage/ContestProblemsPage';
import ContestProblemPage from './single/ContestProblemPage/ContestProblemPage';

const ContestProblemRoutes = () => (
  <div>
    <Route exact path="/contests/:contestId/problems" component={ContestProblemsPage} />
    <Route path="/contests/:contestId/problems/:problemAlias" component={ContestProblemPage} />
  </div>
);

export default withRouter<any>(ContestProblemRoutes);
