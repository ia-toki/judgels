import { Route, withRouter } from 'react-router';

import SingleContestRoutes from './SingleContestRoutes';
import SingleContestDataRoute from './SingleContestDataRoute';

function MainSingleContestRoutes() {
  return (
    <div>
      <Route path="/contests/:contestSlug" component={SingleContestDataRoute} />
      <Route path="/contests/:contestSlug" component={SingleContestRoutes} />
    </div>
  );
}

export default withRouter(MainSingleContestRoutes);
