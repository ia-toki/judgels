import * as React from 'react';
import { Switch, withRouter, Route } from 'react-router';

import GuestRoute from '../../components/GuestRoute/GuestRoute';
import UserRoute from '../../components/UserRoute/UserRoute';
import { HomePage } from './home/HomePage/HomePage';
import { WelcomePage } from './home/WelcomePage/WelcomePage';
import LoginPage from './login/LoginPage/LoginPage';
import LogoutPage from './logout/LogoutPage/LogoutPage';
import RegisterPage from './register/RegisterPage/RegisterPage';
import ActivatePage from './activate/ActivatePage/ActivatePage';
import ForgotPasswordPage from './forgotPassword/ForgotPasswordPage/ForgotPasswordPage';
import ResetPasswordPage from './resetPassword/ResetPasswordPage/ResetPasswordPage';
import JophielAccountRoutes from './JophielAccountRoutes';
import PublicProfilePage from './profiles/PublicProfilePage/PublicProfilePage';

const JophielRoutes = () => (
  <div>
    <Switch>
      <GuestRoute exact path="/" component={WelcomePage} />
      <UserRoute exact path="/home" component={HomePage} />
      <GuestRoute exact path="/login" component={LoginPage} />
      <UserRoute exact path="/logout" component={LogoutPage} />
      <GuestRoute exact path="/register" component={RegisterPage} />
      <GuestRoute exact path="/activate/:emailCode" component={ActivatePage} />
      <GuestRoute exact path="/forgot-password" component={ForgotPasswordPage} />
      <GuestRoute exact path="/reset-password/:emailCode" component={ResetPasswordPage} />
      <UserRoute path="/account" component={JophielAccountRoutes} />
      <Route path="/profiles/:username" component={PublicProfilePage} />
    </Switch>
  </div>
);

export default withRouter<any>(JophielRoutes);
