import { Navigate, createRoute, lazyRouteComponent } from '@tanstack/react-router';

import EnabledRoute from '../../components/EnabledRoute/EnabledRoute';
import { retryImport } from '../../lazy';
import { isTrainingAccountPasswordResetEnabled, isTrainingAccountRegistrationEnabled } from '../../modules/webConfig';
import { createDocumentTitle } from '../../utils/title';

const AccountLayout = lazyRouteComponent(retryImport(() => import('./AccountLayout')));
const InfoPage = lazyRouteComponent(retryImport(() => import('./info/InfoPage/InfoPage')));
const ChangeAvatarPage = lazyRouteComponent(retryImport(() => import('./avatar/ChangeAvatarPage/ChangeAvatarPage')));
const PasswordPage = lazyRouteComponent(retryImport(() => import('./password/PasswordPage/PasswordPage')));

export const createAccountRoutes = appRoute => {
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
        <PasswordPage />
      </EnabledRoute>
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Change password') }] }),
  });

  return accountRoute.addChildren([accountIndexRoute, accountInfoRoute, accountAvatarRoute, accountPasswordRoute]);
};
