import { Route } from 'react-router';

import ProblemsRoutes from './ProblemsRoutes';

function MainProblemsWrapperRoutes() {
  return (
    <div>
      <Route path="/problems" component={ProblemsRoutes} />
    </div>
  );
}

export default MainProblemsWrapperRoutes;
