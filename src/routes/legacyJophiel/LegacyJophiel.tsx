import * as React from 'react';
import { Route, Switch, withRouter } from 'react-router';

import ServiceLoginContainer from './service-login/ServiceLogin/ServiceLogin';
import ServiceLogoutContainer from './service-logout/ServiceLogout/ServiceLogout';
import UserRoute from '../../components/UserRoute/UserRoute';

const LegacyJophielContainer = () => (
  <div>
    <Switch>
      <Route path="/service-login/:redirectUri/:returnUri" component={ServiceLoginContainer} />
      <UserRoute path="/service-logout/:returnUri" component={ServiceLogoutContainer} />
    </Switch>
  </div>
);

export default withRouter<any>(LegacyJophielContainer);
