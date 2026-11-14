import { Navigate, createRoute, lazyRouteComponent } from '@tanstack/react-router';

import EnabledRoute from '../../components/EnabledRoute/EnabledRoute';
import GuestRoute from '../../components/GuestRoute/GuestRoute';
import UserRoute from '../../components/UserRoute/UserRoute';
import { retryImport } from '../../lazy';
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

const AccountLayout = lazyRouteComponent(retryImport(() => import('./account/AccountLayout')));
const InfoPage = lazyRouteComponent(retryImport(() => import('./account/info/InfoPage/InfoPage')));
const ChangeAvatarPage = lazyRouteComponent(
  retryImport(() => import('./account/changeAvatar/ChangeAvatarPage/ChangeAvatarPage'))
);
const AccountResetPasswordPage = lazyRouteComponent(
  retryImport(() => import('./account/resetPassword/ResetPasswordPage/ResetPasswordPage'))
);

export const createAccountRoutes = appRoute => {
  const registeredRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'registered',
    component: () => (
      <EnabledRoute enabled={isTrainingAccountRegistrationEnabled()}>
        <RegisteredPage />
      </EnabledRoute>
    ),
  });

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

  const accountRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'account',
    component: () => (
      <EnabledRoute enabled={isTrainingAccountRegistrationEnabled() || isTrainingAccountPasswordResetEnabled()}>
        <AccountLayout />
      </EnabledRoute>
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('My account') }] }),
  });

  const accountIndexRoute = createRoute({
    getParentRoute: () => accountRoute,
    path: '/',
    component: () => <Navigate to={isTrainingAccountRegistrationEnabled() ? '/account/info' : '/account/password'} />,
  });

  const accountInfoRoute = createRoute({
    getParentRoute: () => accountRoute,
    path: 'info',
    component: () => (
      <EnabledRoute enabled={isTrainingAccountRegistrationEnabled()}>
        <InfoPage />
      </EnabledRoute>
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Info') }] }),
  });

  const accountAvatarRoute = createRoute({
    getParentRoute: () => accountRoute,
    path: 'avatar',
    component: () => (
      <EnabledRoute enabled={isTrainingAccountRegistrationEnabled()}>
        <ChangeAvatarPage />
      </EnabledRoute>
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Change avatar') }] }),
  });

  const accountPasswordRoute = createRoute({
    getParentRoute: () => accountRoute,
    path: 'password',
    component: () => (
      <EnabledRoute enabled={isTrainingAccountPasswordResetEnabled()}>
        <AccountResetPasswordPage />
      </EnabledRoute>
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Change password') }] }),
  });

  return [
    registeredRoute,
    loginRoute,
    logoutRoute,
    registerRoute,
    activateRoute,
    forgotPasswordRoute,
    needActivationRoute,
    resetPasswordRoute,
    accountRoute.addChildren([accountIndexRoute, accountInfoRoute, accountAvatarRoute, accountPasswordRoute]),
  ];
};
