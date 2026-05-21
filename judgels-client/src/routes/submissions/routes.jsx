import { createRoute, lazyRouteComponent, redirect } from '@tanstack/react-router';

import { retryImport } from '../../lazy';
import { submissionWithSourceQueryOptions, submissionsQueryOptions } from '../../modules/queries/submissionProgramming';
import { queryClient } from '../../modules/queryClient';
import { getUser } from '../../modules/session';
import { getWebPrefs } from '../../modules/webPrefs';
import { createDocumentTitle } from '../../utils/title';
import { isUserBlocked } from '../blockedUsernames';

export const createSubmissionsRoutes = appRoute => {
  const submissionsRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'submissions',
    component: lazyRouteComponent(retryImport(() => import('./SubmissionsLayout'))),
    head: () => ({ meta: [{ title: createDocumentTitle('Submissions') }] }),
    beforeLoad: () => {
      if (isUserBlocked(getUser())) {
        throw redirect({ to: '/' });
      }
    },
  });

  const submissionsIndexRoute = createRoute({
    getParentRoute: () => submissionsRoute,
    path: '/',
    component: lazyRouteComponent(retryImport(() => import('./SubmissionsPage/SubmissionsPage'))),
    loader: ({ search = {} }) => {
      queryClient.prefetchQuery(submissionsQueryOptions({ beforeId: search.before, afterId: search.after }));
    },
  });

  const submissionsMineRoute = createRoute({
    getParentRoute: () => submissionsRoute,
    path: 'mine',
    component: lazyRouteComponent(retryImport(() => import('./SubmissionsPage/SubmissionsPage'))),
    loader: ({ search = {} }) => {
      const username = getUser()?.username;
      queryClient.prefetchQuery(submissionsQueryOptions({ username, beforeId: search.before, afterId: search.after }));
    },
  });

  const singleSubmissionRoute = createRoute({
    getParentRoute: () => submissionsRoute,
    path: '$submissionId',
    component: lazyRouteComponent(retryImport(() => import('./single/SubmissionPage/SubmissionPage'))),
    loader: ({ params: { submissionId } }) => {
      const language = getWebPrefs().statementLanguage;
      queryClient.prefetchQuery(submissionWithSourceQueryOptions(+submissionId, { language }));
    },
  });

  return submissionsRoute.addChildren([submissionsIndexRoute, submissionsMineRoute, singleSubmissionRoute]);
};
