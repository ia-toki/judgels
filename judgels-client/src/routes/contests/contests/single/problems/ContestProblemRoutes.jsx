import { Route } from 'react-router';

import ContestProblemsPage from './ContestProblemsPage/ContestProblemsPage';
import ContestProblemPage from './single/ContestProblemPage/ContestProblemPage';

export default function ContestProblemRoutes() {
  return (
    <div>
      <Route exact path="/contests/:contestSlug/problems" component={ContestProblemsPage} />
      <Route path="/contests/:contestSlug/problems/:problemAlias" component={ContestProblemPage} />
    </div>
  );
}
