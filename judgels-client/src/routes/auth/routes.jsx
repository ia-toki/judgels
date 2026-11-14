import { createRoute } from '@tanstack/react-router';

import EnabledRoute from '../../components/EnabledRoute/EnabledRoute';
import GuestRoute from '../../components/GuestRoute/GuestRoute';
import UserRoute from '../../components/UserRoute/UserRoute';
import { isTrainingAccountPasswordResetEnabled, isTrainingAccountRegistrationEnabled } from '../../modules/webConfig';
import { createDocumentTitle } from '../../utils/title';
import ActivatePage from './activate/ActivatePage/ActivatePage';
import ForgotPasswordPage from './forgotPassword/ForgotPasswordPage/ForgotPasswordPage';
import LoginPage from './login/LoginPage/LoginPage';
import LogoutPage from './logout/LogoutPage/LogoutPage';
import NeedActivationPage from './needActivation/NeedActivationPage/NeedActivationPage';
import RegisterPage from './register/RegisterPage/RegisterPage';
import RegisteredPage from './registered/RegisteredPage/RegisteredPage';
import ResetPasswordPage from './resetPassword/ResetPasswordPage/ResetPasswordPage';

export const createAuthRoutes = appRoute => {
  const loginRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'login',
    component: () => (
      <GuestRoute>
        <LoginPage />
      </GuestRoute>
    ),
  });

  const logoutRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'logout',
    component: () => (
      <UserRoute>
        <LogoutPage />
      </UserRoute>
    ),
  });

  const registerRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'register',
    component: () => (
      <EnabledRoute enabled={isTrainingAccountRegistrationEnabled()}>
        <GuestRoute>
          <RegisterPage />
        </GuestRoute>
      </EnabledRoute>
    ),
  });

  const registeredRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'registered',
    component: () => (
      <EnabledRoute enabled={isTrainingAccountRegistrationEnabled()}>
        <RegisteredPage />
      </EnabledRoute>
    ),
  });

  const needActivationRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'need-activation',
    component: () => (
      <EnabledRoute enabled={isTrainingAccountRegistrationEnabled()}>
        <GuestRoute>
          <NeedActivationPage />
        </GuestRoute>
      </EnabledRoute>
    ),
  });

  const activateRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'activate/$emailCode',
    component: () => (
      <EnabledRoute enabled={isTrainingAccountRegistrationEnabled()}>
        <GuestRoute>
          <ActivatePage />
        </GuestRoute>
      </EnabledRoute>
    ),
  });

  const forgotPasswordRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'forgot-password',
    component: () => (
      <EnabledRoute enabled={isTrainingAccountPasswordResetEnabled()}>
        <GuestRoute>
          <ForgotPasswordPage />
        </GuestRoute>
      </EnabledRoute>
    ),
  });

  const resetPasswordRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'reset-password/$emailCode',
    component: () => (
      <EnabledRoute enabled={isTrainingAccountPasswordResetEnabled()}>
        <GuestRoute>
          <ResetPasswordPage />
        </GuestRoute>
      </EnabledRoute>
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Reset password') }] }),
  });

  return [
    loginRoute,
    logoutRoute,
    registerRoute,
    registeredRoute,
    needActivationRoute,
    activateRoute,
    forgotPasswordRoute,
    resetPasswordRoute,
  ];
};
