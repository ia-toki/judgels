import { Route, withRouter } from 'react-router';

import SingleContestDataRoute from './SingleContestDataRoute';
import SingleContestRoutes from './SingleContestRoutes';

function MainSingleContestRoutes() {
  return (
    <div>
      <Route path="/contests/:contestSlug" component={SingleContestDataRoute} />
      <Route path="/contests/:contestSlug" component={SingleContestRoutes} />
    </div>
  );
}

export default withRouter(MainSingleContestRoutes);
