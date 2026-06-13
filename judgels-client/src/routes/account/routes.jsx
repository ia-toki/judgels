import { Navigate, createRoute, lazyRouteComponent } from '@tanstack/react-router';

import GuestRoute from '../../components/GuestRoute/GuestRoute';
import UserRoute from '../../components/UserRoute/UserRoute';
import { retryImport } from '../../lazy';
import { createDocumentTitle } from '../../utils/title';
import ActivatePage from './activate/ActivatePage/ActivatePage';
import ForgotPasswordPage from './forgotPassword/ForgotPasswordPage/ForgotPasswordPage';
import LoginPage from './login/LoginPage/LoginPage';
import LogoutPage from './logout/LogoutPage/LogoutPage';
import NeedActivationPage from './needActivation/NeedActivationPage/NeedActivationPage';
import RegisterPage from './register/RegisterPage/RegisterPage';
import RegisteredPage from './registered/RegisteredPage/RegisteredPage';
import ResetPasswordPage from './resetPassword/ResetPasswordPage/ResetPasswordPage';

export const createAccountRoutes = appRoute => {
  const registeredRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'registered',
    component: RegisteredPage,
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
      <GuestRoute>
        <RegisterPage />
      </GuestRoute>
    ),
  });

  const activateRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'activate/$emailCode',
    component: () => (
      <GuestRoute>
        <ActivatePage />
      </GuestRoute>
    ),
  });

  const forgotPasswordRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'forgot-password',
    component: () => (
      <GuestRoute>
        <ForgotPasswordPage />
      </GuestRoute>
    ),
  });

  const needActivationRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'need-activation',
    component: () => (
      <GuestRoute>
        <NeedActivationPage />
      </GuestRoute>
    ),
  });

  const resetPasswordRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'reset-password/$emailCode',
    component: () => (
      <GuestRoute>
        <ResetPasswordPage />
      </GuestRoute>
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Reset password') }] }),
  });

  const accountRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'account',
    component: lazyRouteComponent(retryImport(() => import('./account/AccountLayout'))),
    head: () => ({ meta: [{ title: createDocumentTitle('My account') }] }),
  });

  const accountIndexRoute = createRoute({
    getParentRoute: () => accountRoute,
    path: '/',
    component: () => <Navigate to="/account/info" />,
  });

  const accountInfoRoute = createRoute({
    getParentRoute: () => accountRoute,
    path: 'info',
    component: lazyRouteComponent(retryImport(() => import('./account/info/InfoPage/InfoPage'))),
    head: () => ({ meta: [{ title: createDocumentTitle('Info') }] }),
  });

  const accountAvatarRoute = createRoute({
    getParentRoute: () => accountRoute,
    path: 'avatar',
    component: lazyRouteComponent(
      retryImport(() => import('./account/changeAvatar/ChangeAvatarPage/ChangeAvatarPage'))
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Change avatar') }] }),
  });

  const accountPasswordRoute = createRoute({
    getParentRoute: () => accountRoute,
    path: 'password',
    component: lazyRouteComponent(
      retryImport(() => import('./account/resetPassword/ResetPasswordPage/ResetPasswordPage'))
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
