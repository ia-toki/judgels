import { Route, withRouter } from 'react-router';

import SingleProblemSetProblemRoutes from './SingleProblemSetProblemRoutes';
import SingleProblemSetProblemDataRoute from './SingleProblemSetProblemDataRoute';

function MainSingleProblemSetProblemRoutes() {
  return (
    <div>
      <Route path="/problems/:problemSetSlug/:problemAlias" component={SingleProblemSetProblemDataRoute} />
      <Route path="/problems/:problemSetSlug/:problemAlias" component={SingleProblemSetProblemRoutes} />
    </div>
  );
}

export default withRouter(MainSingleProblemSetProblemRoutes);
