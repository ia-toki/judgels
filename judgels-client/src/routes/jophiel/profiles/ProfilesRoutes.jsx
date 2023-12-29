import { Route, withRouter } from 'react-router';

import { withBreadcrumb } from '../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import SingleProfileDataRoute from './single/SingleProfileDataRoute';
import SingleProfileRoutes from './single/SingleProfileRoutes';

function ProfileRoutes() {
  return (
    <div>
      <Route path="/profiles/:username" component={SingleProfileDataRoute} />
      <Route path="/profiles/:username" component={SingleProfileRoutes} />
    </div>
  );
}

export default withBreadcrumb('Profiles')(withRouter(ProfileRoutes));
