import { Route } from 'react-router';

import SingleContestDataRoute from './SingleContestDataRoute';
import SingleContestRoutes from './SingleContestRoutes';

export default function MainSingleContestRoutes() {
  return (
    <div>
      <Route path="/contests/:contestSlug" component={SingleContestDataRoute} />
      <Route path="/contests/:contestSlug" component={SingleContestRoutes} />
    </div>
  );
}
