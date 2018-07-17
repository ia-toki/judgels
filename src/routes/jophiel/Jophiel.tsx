import * as React from 'react';
import { Switch, withRouter } from 'react-router';

import GuestRoute from '../../components/GuestRoute/GuestRoute';
import UserRoute from '../../components/UserRoute/UserRoute';
import { Home } from './home/Home/Home';
import LoginContainer from './login/Login/Login';
import LogoutContainer from './logout/Logout/Logout';
import RegisterContainer from './register/Register/Register';
import ActivateContainer from './activate/Activate/Activate';
import ForgotPasswordContainer from './forgotPassword/ForgotPassword/ForgotPassword';
import ResetPasswordContainer from './resetPassword/ResetPassword/ResetPassword';
import AccountContainer from './account/Account';

const JophielContainer = () => (
  <div>
    <Switch>
      <UserRoute exact path="/" component={Home} />
      <GuestRoute exact path="/login" component={LoginContainer} />
      <UserRoute exact path="/logout" component={LogoutContainer} />
      <GuestRoute exact path="/register" component={RegisterContainer} />
      <GuestRoute exact path="/activate/:emailCode" component={ActivateContainer} />
      <GuestRoute exact path="/forgot-password" component={ForgotPasswordContainer} />
      <GuestRoute exact path="/reset-password/:emailCode" component={ResetPasswordContainer} />
      <UserRoute path="/account" component={AccountContainer} />
    </Switch>
  </div>
);

export default withRouter<any>(JophielContainer);
