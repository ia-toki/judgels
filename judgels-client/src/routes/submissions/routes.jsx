import { createRoute, lazyRouteComponent } from '@tanstack/react-router';

import { createDocumentTitle } from '../../utils/title';

export const createSubmissionsRoutes = appRoute => {
  const submissionsRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'submissions',
    component: lazyRouteComponent(() => import('./SubmissionsLayout')),
    head: () => ({ meta: [{ title: createDocumentTitle('Submissions') }] }),
  });

  const submissionsIndexRoute = createRoute({
    getParentRoute: () => submissionsRoute,
    path: '/',
    component: lazyRouteComponent(() => import('./SubmissionsPage/SubmissionsPage')),
  });

  const submissionsMineRoute = createRoute({
    getParentRoute: () => submissionsRoute,
    path: 'mine',
    component: lazyRouteComponent(() => import('./SubmissionsPage/SubmissionsPage')),
  });

  const singleSubmissionRoute = createRoute({
    getParentRoute: () => submissionsRoute,
    path: '$submissionId',
    component: lazyRouteComponent(() => import('./single/SubmissionPage/SubmissionPage')),
  });

  return submissionsRoute.addChildren([submissionsIndexRoute, submissionsMineRoute, singleSubmissionRoute]);
};
