import * as React from 'react';
import { Route, withRouter } from 'react-router';

import SingleProfileDataRoute from './single/routes/SingleProfileDataRoute';
import SingleProfileRoutes from './single/routes/SingleProfileRoutes';
import { withBreadcrumb } from '../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';

const ProfileRoutes = () => (
  <div>
    <Route path="/profiles/:username" component={SingleProfileDataRoute} />
    <Route path="/profiles/:username" component={SingleProfileRoutes} />
  </div>
);

export default withBreadcrumb('Profiles')(withRouter<any>(ProfileRoutes));
