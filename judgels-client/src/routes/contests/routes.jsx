import { Outlet, createRoute, lazyRouteComponent } from '@tanstack/react-router';

import { retryImport } from '../../lazy';
import { createDocumentTitle } from '../../utils/title';

export const createContestsRoutes = appRoute => {
  const contestsRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'contests',
    component: Outlet,
    head: () => ({ meta: [{ title: createDocumentTitle('Contests') }] }),
  });

  const contestsIndexRoute = createRoute({
    getParentRoute: () => contestsRoute,
    path: '/',
    component: lazyRouteComponent(retryImport(() => import('./ContestsIndexPage'))),
  });

  const contestRoute = createRoute({
    getParentRoute: () => contestsRoute,
    path: '$contestSlug',
    component: lazyRouteComponent(retryImport(() => import('./contests/single/SingleContestLayout'))),
  });

  const contestOverviewRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: '/',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/overview/ContestOverviewPage/ContestOverviewPage'))
    ),
  });

  const contestAnnouncementsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'announcements',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/announcements/ContestAnnouncementsPage/ContestAnnouncementsPage'))
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Announcements') }] }),
  });

  const contestProblemsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'problems',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/problems/ContestProblemsPage/ContestProblemsPage'))
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Problems') }] }),
  });

  const contestProblemRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'problems/$problemAlias',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/problems/single/ContestProblemPage/ContestProblemPage'))
    ),
  });

  const contestEditorialRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'editorial',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/editorial/ContestEditorialPage/ContestEditorialPage'))
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Editorial') }] }),
  });

  const contestContestantsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'contestants',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/contestants/ContestContestantsPage/ContestContestantsPage'))
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Contestants') }] }),
  });

  const contestSupervisorsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'supervisors',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/supervisors/ContestSupervisorsPage/ContestSupervisorsPage'))
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Supervisors') }] }),
  });

  const contestManagersRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'managers',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/managers/ContestManagersPage/ContestManagersPage'))
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Managers') }] }),
  });

  const contestSubmissionsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'submissions',
    component: Outlet,
    head: () => ({ meta: [{ title: createDocumentTitle('Submissions') }] }),
  });

  const contestSubmissionsIndexRoute = createRoute({
    getParentRoute: () => contestSubmissionsRoute,
    path: '/',
    component: lazyRouteComponent(retryImport(() => import('./contests/single/submissions/ContestSubmissionsPage'))),
  });

  const contestSubmissionRoute = createRoute({
    getParentRoute: () => contestSubmissionsRoute,
    path: '$submissionId',
    component: lazyRouteComponent(
      retryImport(
        () => import('./contests/single/submissions/Programming/single/ContestSubmissionPage/ContestSubmissionPage')
      )
    ),
  });

  const contestSubmissionUserRoute = createRoute({
    getParentRoute: () => contestSubmissionsRoute,
    path: 'users/$username',
    component: lazyRouteComponent(
      retryImport(
        () => import('./contests/single/submissions/Bundle/ContestSubmissionSummaryPage/ContestSubmissionSummaryPage')
      )
    ),
  });

  const contestClarificationsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'clarifications',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/clarifications/ContestClarificationsPage/ContestClarificationsPage'))
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Clarifications') }] }),
  });

  const contestScoreboardRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'scoreboard',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/scoreboard/ContestScoreboardPage/ContestScoreboardPage'))
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Scoreboard') }] }),
  });

  const contestFilesRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'files',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/files/ContestFilesPage/ContestFilesPage'))
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Files') }] }),
  });

  const contestLogsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'logs',
    component: lazyRouteComponent(retryImport(() => import('./contests/single/logs/ContestLogsPage/ContestLogsPage'))),
    head: () => ({ meta: [{ title: createDocumentTitle('Logs') }] }),
  });

  return contestsRoute.addChildren([
    contestsIndexRoute,
    contestRoute.addChildren([
      contestOverviewRoute,
      contestAnnouncementsRoute,
      contestProblemsRoute,
      contestProblemRoute,
      contestEditorialRoute,
      contestContestantsRoute,
      contestSupervisorsRoute,
      contestManagersRoute,
      contestSubmissionsRoute.addChildren([
        contestSubmissionsIndexRoute,
        contestSubmissionRoute,
        contestSubmissionUserRoute,
      ]),
      contestClarificationsRoute,
      contestScoreboardRoute,
      contestFilesRoute,
      contestLogsRoute,
    ]),
  ]);
};
