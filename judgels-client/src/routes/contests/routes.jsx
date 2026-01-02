import { Outlet, createRoute, lazyRouteComponent } from '@tanstack/react-router';

export const createContestsRoutes = appRoute => {
  const contestsRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'contests',
    component: lazyRouteComponent(() => import('./ContestsLayout')),
  });

  const contestsIndexRoute = createRoute({
    getParentRoute: () => contestsRoute,
    path: '/',
    component: lazyRouteComponent(() => import('./ContestsIndexPage')),
  });

  const contestRoute = createRoute({
    getParentRoute: () => contestsRoute,
    path: '$contestSlug',
    component: lazyRouteComponent(() => import('./contests/single/SingleContestLayout')),
  });

  const contestOverviewRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: '/',
    component: lazyRouteComponent(() => import('./contests/single/overview/ContestOverviewPage/ContestOverviewPage')),
  });

  const contestAnnouncementsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'announcements',
    component: lazyRouteComponent(
      () => import('./contests/single/announcements/ContestAnnouncementsPage/ContestAnnouncementsPage')
    ),
  });

  const contestProblemsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'problems',
    component: lazyRouteComponent(() => import('./contests/single/problems/ContestProblemsPage/ContestProblemsPage')),
  });

  const contestProblemRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'problems/$problemAlias',
    component: lazyRouteComponent(
      () => import('./contests/single/problems/single/ContestProblemPage/ContestProblemPage')
    ),
  });

  const contestEditorialRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'editorial',
    component: lazyRouteComponent(
      () => import('./contests/single/editorial/ContestEditorialPage/ContestEditorialPage')
    ),
  });

  const contestContestantsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'contestants',
    component: lazyRouteComponent(
      () => import('./contests/single/contestants/ContestContestantsPage/ContestContestantsPage')
    ),
  });

  const contestSupervisorsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'supervisors',
    component: lazyRouteComponent(
      () => import('./contests/single/supervisors/ContestSupervisorsPage/ContestSupervisorsPage')
    ),
  });

  const contestManagersRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'managers',
    component: lazyRouteComponent(() => import('./contests/single/managers/ContestManagersPage/ContestManagersPage')),
  });

  const contestSubmissionsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'submissions',
    component: Outlet,
  });

  const contestSubmissionsIndexRoute = createRoute({
    getParentRoute: () => contestSubmissionsRoute,
    path: '/',
    component: lazyRouteComponent(() => import('./contests/single/submissions/ContestSubmissionsPage')),
  });

  const contestSubmissionRoute = createRoute({
    getParentRoute: () => contestSubmissionsRoute,
    path: '$submissionId',
    component: lazyRouteComponent(
      () => import('./contests/single/submissions/Programming/single/ContestSubmissionPage/ContestSubmissionPage')
    ),
  });

  const contestSubmissionUserRoute = createRoute({
    getParentRoute: () => contestSubmissionsRoute,
    path: 'users/$username',
    component: lazyRouteComponent(
      () => import('./contests/single/submissions/Bundle/ContestSubmissionSummaryPage/ContestSubmissionSummaryPage')
    ),
  });

  const contestClarificationsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'clarifications',
    component: lazyRouteComponent(
      () => import('./contests/single/clarifications/ContestClarificationsPage/ContestClarificationsPage')
    ),
  });

  const contestScoreboardRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'scoreboard',
    component: lazyRouteComponent(
      () => import('./contests/single/scoreboard/ContestScoreboardPage/ContestScoreboardPage')
    ),
  });

  const contestFilesRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'files',
    component: lazyRouteComponent(() => import('./contests/single/files/ContestFilesPage/ContestFilesPage')),
  });

  const contestLogsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'logs',
    component: lazyRouteComponent(() => import('./contests/single/logs/ContestLogsPage/ContestLogsPage')),
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
