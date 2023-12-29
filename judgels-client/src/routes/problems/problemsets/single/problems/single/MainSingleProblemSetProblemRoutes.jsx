import { Route, withRouter } from 'react-router';

import SingleProblemSetProblemDataRoute from './SingleProblemSetProblemDataRoute';
import SingleProblemSetProblemRoutes from './SingleProblemSetProblemRoutes';

function MainSingleProblemSetProblemRoutes() {
  return (
    <div>
      <Route path="/problems/:problemSetSlug/:problemAlias" component={SingleProblemSetProblemDataRoute} />
      <Route path="/problems/:problemSetSlug/:problemAlias" component={SingleProblemSetProblemRoutes} />
    </div>
  );
}

export default withRouter(MainSingleProblemSetProblemRoutes);
