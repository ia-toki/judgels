import { Route, Switch } from 'react-router';

import SingleProblemSetDataRoute from './SingleProblemSetDataRoute';
import SingleProblemSetRoutes from './SingleProblemSetRoutes';
import MainSingleProblemSetProblemRoutes from './problems/single/MainSingleProblemSetProblemRoutes';

function MainSingleProblemSetRoutes() {
  return (
    <div>
      <Route path="/problems/:problemSetSlug" component={SingleProblemSetDataRoute} />
      <Switch>
        <Route path="/problems/:problemSetSlug/:problemAlias" component={MainSingleProblemSetProblemRoutes} />
        <Route path="/problems/:problemSetSlug" component={SingleProblemSetRoutes} />
      </Switch>
    </div>
  );
}

export default MainSingleProblemSetRoutes;
