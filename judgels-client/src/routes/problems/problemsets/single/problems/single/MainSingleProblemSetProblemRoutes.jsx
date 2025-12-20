import { Route } from 'react-router';

import SingleProblemSetProblemDataRoute from './SingleProblemSetProblemDataRoute';
import SingleProblemSetProblemRoutes from './SingleProblemSetProblemRoutes';

export default function MainSingleProblemSetProblemRoutes() {
  return (
    <div>
      <Route path="/problems/:problemSetSlug/:problemAlias" component={SingleProblemSetProblemDataRoute} />
      <Route path="/problems/:problemSetSlug/:problemAlias" component={SingleProblemSetProblemRoutes} />
    </div>
  );
}
