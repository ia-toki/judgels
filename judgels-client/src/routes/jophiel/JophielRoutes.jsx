import { Outlet } from 'react-router';

import GuestRoute from '../../components/GuestRoute/GuestRoute';
import UserRoute from '../../components/UserRoute/UserRoute';
import HomePage from '../home/HomePage/HomePage';
import { accountRoutes } from './account/AccountRoutes';
import ActivatePage from './activate/ActivatePage/ActivatePage';
import ForgotPasswordPage from './forgotPassword/ForgotPasswordPage/ForgotPasswordPage';
import LoginPage from './login/LoginPage/LoginPage';
import LogoutPage from './logout/LogoutPage/LogoutPage';
import NeedActivationPage from './needActivation/NeedActivationPage/NeedActivationPage';
import { profilesRoutes } from './profiles/ProfilesRoutes';
import RegisterPage from './register/RegisterPage/RegisterPage';
import RegisteredPage from './registered/RegisteredPage/RegisteredPage';
import ResetPasswordPage from './resetPassword/ResetPasswordPage/ResetPasswordPage';

function JophielLayout() {
  return (
    <div>
      <Outlet />
    </div>
  );
}

function UserRouteLayout() {
  return (
    <UserRoute>
      <Outlet />
    </UserRoute>
  );
}

export const jophielRoutes = {
  element: <JophielLayout />,
  children: [
    {
      index: true,
      element: <HomePage />,
    },
    {
      path: 'registered',
      element: <RegisteredPage />,
    },
    {
      path: 'login',
      element: (
        <GuestRoute>
          <LoginPage />
        </GuestRoute>
      ),
    },
    {
      path: 'logout',
      element: (
        <UserRoute>
          <LogoutPage />
        </UserRoute>
      ),
    },
    {
      path: 'register',
      element: (
        <GuestRoute>
          <RegisterPage />
        </GuestRoute>
      ),
    },
    {
      path: 'activate/:emailCode',
      element: (
        <GuestRoute>
          <ActivatePage />
        </GuestRoute>
      ),
    },
    {
      path: 'forgot-password',
      element: (
        <GuestRoute>
          <ForgotPasswordPage />
        </GuestRoute>
      ),
    },
    {
      path: 'need-activation',
      element: (
        <GuestRoute>
          <NeedActivationPage />
        </GuestRoute>
      ),
    },
    {
      path: 'reset-password/:emailCode',
      element: (
        <GuestRoute>
          <ResetPasswordPage />
        </GuestRoute>
      ),
    },
    {
      path: 'account',
      element: <UserRouteLayout />,
      children: accountRoutes,
    },
    {
      path: 'profiles',
      children: profilesRoutes,
    },
  ],
};
