import * as React from 'react';
import { Route, Switch, withRouter } from 'react-router';

import ServiceLoginPage from './service-login/ServiceLoginPage/ServiceLoginPage';
import ServiceLogoutPage from './service-logout/ServiceLogoutPage/ServiceLogoutPage';
import UserRoute from '../../components/UserRoute/UserRoute';

const LegacyJophielRoutes = () => (
  <div>
    <Switch>
      <Route path="/service-login/:redirectUri/:returnUri" component={ServiceLoginPage} />
      <UserRoute path="/service-logout/:returnUri" component={ServiceLogoutPage} />
    </Switch>
  </div>
);

export default withRouter<any, any>(LegacyJophielRoutes);
