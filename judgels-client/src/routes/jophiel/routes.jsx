import { Navigate, Outlet, createRoute, lazyRouteComponent } from '@tanstack/react-router';

import GuestRoute from '../../components/GuestRoute/GuestRoute';
import UserRoute from '../../components/UserRoute/UserRoute';
import { isTLX } from '../../conf';
import { retryImport } from '../../lazy';
import { createDocumentTitle } from '../../utils/title';
import HomePage from '../home/HomePage/HomePage';
import ActivatePage from './activate/ActivatePage/ActivatePage';
import ForgotPasswordPage from './forgotPassword/ForgotPasswordPage/ForgotPasswordPage';
import LoginPage from './login/LoginPage/LoginPage';
import LogoutPage from './logout/LogoutPage/LogoutPage';
import NeedActivationPage from './needActivation/NeedActivationPage/NeedActivationPage';
import RegisterPage from './register/RegisterPage/RegisterPage';
import RegisteredPage from './registered/RegisteredPage/RegisteredPage';
import ResetPasswordPage from './resetPassword/ResetPasswordPage/ResetPasswordPage';

export const createJophielRoutes = appRoute => {
  const jophielRoute = createRoute({
    getParentRoute: () => appRoute,
    id: 'jophiel',
    component: Outlet,
  });

  const homeRoute = createRoute({
    getParentRoute: () => jophielRoute,
    path: '/',
    component: HomePage,
  });

  const registeredRoute = createRoute({
    getParentRoute: () => jophielRoute,
    path: 'registered',
    component: RegisteredPage,
  });

  const loginRoute = createRoute({
    getParentRoute: () => jophielRoute,
    path: 'login',
    component: () => (
      <GuestRoute>
        <LoginPage />
      </GuestRoute>
    ),
  });

  const logoutRoute = createRoute({
    getParentRoute: () => jophielRoute,
    path: 'logout',
    component: () => (
      <UserRoute>
        <LogoutPage />
      </UserRoute>
    ),
  });

  const registerRoute = createRoute({
    getParentRoute: () => jophielRoute,
    path: 'register',
    component: () => (
      <GuestRoute>
        <RegisterPage />
      </GuestRoute>
    ),
  });

  const activateRoute = createRoute({
    getParentRoute: () => jophielRoute,
    path: 'activate/$emailCode',
    component: () => (
      <GuestRoute>
        <ActivatePage />
      </GuestRoute>
    ),
  });

  const forgotPasswordRoute = createRoute({
    getParentRoute: () => jophielRoute,
    path: 'forgot-password',
    component: () => (
      <GuestRoute>
        <ForgotPasswordPage />
      </GuestRoute>
    ),
  });

  const needActivationRoute = createRoute({
    getParentRoute: () => jophielRoute,
    path: 'need-activation',
    component: () => (
      <GuestRoute>
        <NeedActivationPage />
      </GuestRoute>
    ),
  });

  const resetPasswordRoute = createRoute({
    getParentRoute: () => jophielRoute,
    path: 'reset-password/$emailCode',
    component: () => (
      <GuestRoute>
        <ResetPasswordPage />
      </GuestRoute>
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Reset password') }] }),
  });

  const accountRoute = createRoute({
    getParentRoute: () => jophielRoute,
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

  const profilesRoute = createRoute({
    getParentRoute: () => jophielRoute,
    path: 'profiles/$username',
    component: lazyRouteComponent(retryImport(() => import('./profiles/ProfilesLayout'))),
    head: () => ({ meta: [{ title: createDocumentTitle('Profiles') }] }),
  });

  const profileIndexRoute = createRoute({
    getParentRoute: () => profilesRoute,
    path: '/',
    component: lazyRouteComponent(
      retryImport(() => import('./profiles/single/summary/ProfileSummaryPage/ProfileSummaryPage'))
    ),
  });

  const profileContestHistoryRoute = createRoute({
    getParentRoute: () => profilesRoute,
    path: 'contest-history',
    component: lazyRouteComponent(
      retryImport(() => import('./profiles/single/contestHistory/ContestHistoryPage/ContestHistoryPage'))
    ),
  });

  const profileSubmissionHistoryRoute = isTLX()
    ? createRoute({
        getParentRoute: () => profilesRoute,
        path: 'submission-history',
        component: lazyRouteComponent(
          retryImport(() => import('./profiles/single/submissionHistory/SubmissionHistoryPage/SubmissionHistoryPage'))
        ),
      })
    : null;

  return jophielRoute.addChildren([
    homeRoute,
    registeredRoute,
    loginRoute,
    logoutRoute,
    registerRoute,
    activateRoute,
    forgotPasswordRoute,
    needActivationRoute,
    resetPasswordRoute,
    accountRoute.addChildren([accountIndexRoute, accountInfoRoute, accountAvatarRoute, accountPasswordRoute]),
    profilesRoute.addChildren(
      [profileIndexRoute, profileContestHistoryRoute, profileSubmissionHistoryRoute].filter(Boolean)
    ),
  ]);
};
