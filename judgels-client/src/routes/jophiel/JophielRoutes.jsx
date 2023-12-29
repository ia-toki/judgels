import { Route, Switch, withRouter } from 'react-router';

import GuestRoute from '../../components/GuestRoute/GuestRoute';
import UserRoute from '../../components/UserRoute/UserRoute';
import HomePage from '../home/HomePage/HomePage';
import JophielAccountRoutes from './JophielAccountRoutes';
import JophielProfilesRoutes from './JophielProfilesRoutes';
import ActivatePage from './activate/ActivatePage/ActivatePage';
import ForgotPasswordPage from './forgotPassword/ForgotPasswordPage/ForgotPasswordPage';
import LoginPage from './login/LoginPage/LoginPage';
import LogoutPage from './logout/LogoutPage/LogoutPage';
import NeedActivationPage from './needActivation/NeedActivationPage/NeedActivationPage';
import RegisterPage from './register/RegisterPage/RegisterPage';
import RegisteredPage from './registered/RegisteredPage/RegisteredPage';
import ResetPasswordPage from './resetPassword/ResetPasswordPage/ResetPasswordPage';

function JophielRoutes() {
  return (
    <div>
      <Switch>
        <Route exact path="/" component={HomePage} />
        <Route exact path="/registered" component={RegisteredPage} />
        <GuestRoute exact path="/login" component={LoginPage} />
        <UserRoute exact path="/logout" component={LogoutPage} />
        <GuestRoute exact path="/register" component={RegisterPage} />
        <GuestRoute exact path="/activate/:emailCode" component={ActivatePage} />
        <GuestRoute exact path="/forgot-password" component={ForgotPasswordPage} />
        <GuestRoute exact path="/need-activation" component={NeedActivationPage} />
        <GuestRoute exact path="/reset-password/:emailCode" component={ResetPasswordPage} />
        <UserRoute path="/account" component={JophielAccountRoutes} />
        <Route path="/profiles" component={JophielProfilesRoutes} />
      </Switch>
    </div>
  );
}

export default withRouter(JophielRoutes);
