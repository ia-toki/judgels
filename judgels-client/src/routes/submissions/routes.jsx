import { createRoute, lazyRouteComponent } from '@tanstack/react-router';

import { retryImport } from '../../lazy';
import { createDocumentTitle } from '../../utils/title';

export const createSubmissionsRoutes = appRoute => {
  const submissionsRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'submissions',
    component: lazyRouteComponent(retryImport(() => import('./SubmissionsLayout'))),
    head: () => ({ meta: [{ title: createDocumentTitle('Submissions') }] }),
  });

  const submissionsIndexRoute = createRoute({
    getParentRoute: () => submissionsRoute,
    path: '/',
    component: lazyRouteComponent(retryImport(() => import('./SubmissionsPage/SubmissionsPage'))),
  });

  const submissionsMineRoute = createRoute({
    getParentRoute: () => submissionsRoute,
    path: 'mine',
    component: lazyRouteComponent(retryImport(() => import('./SubmissionsPage/SubmissionsPage'))),
  });

  const singleSubmissionRoute = createRoute({
    getParentRoute: () => submissionsRoute,
    path: '$submissionId',
    component: lazyRouteComponent(retryImport(() => import('./single/SubmissionPage/SubmissionPage'))),
  });

  return submissionsRoute.addChildren([submissionsIndexRoute, submissionsMineRoute, singleSubmissionRoute]);
};
