import { createRoute, lazyRouteComponent } from '@tanstack/react-router';

export const createProblemsRoutes = appRoute => {
  const problemsRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'problems',
    component: lazyRouteComponent(() => import('./ProblemsLayout')),
  });

  const problemsIndexRoute = createRoute({
    getParentRoute: () => problemsRoute,
    id: 'problems-index',
    component: lazyRouteComponent(() => import('./ProblemsIndexLayout')),
  });

  const problemsProblemsRoute = createRoute({
    getParentRoute: () => problemsIndexRoute,
    path: '/',
    component: lazyRouteComponent(() => import('./problems/ProblemsPage/ProblemsPage')),
  });

  const problemSetsRoute = createRoute({
    getParentRoute: () => problemsIndexRoute,
    path: 'problemsets',
    component: lazyRouteComponent(() => import('./problemsets/ProblemSetsPage/ProblemSetsPage')),
  });

  const problemSetRoute = createRoute({
    getParentRoute: () => problemsRoute,
    path: '$problemSetSlug',
    component: lazyRouteComponent(() => import('./problemsets/single/SingleProblemSetLayout')),
  });

  const problemSetIndexRoute = createRoute({
    getParentRoute: () => problemSetRoute,
    path: '/',
    component: lazyRouteComponent(
      () => import('./problemsets/single/problems/ProblemSetProblemsPage/ProblemSetProblemsPage')
    ),
  });

  const problemSetProblemRoute = createRoute({
    getParentRoute: () => problemsRoute,
    path: '$problemSetSlug/$problemAlias',
    component: lazyRouteComponent(() => import('./problemsets/single/problems/single/SingleProblemSetProblemLayout')),
  });

  const problemSetProblemIndexRoute = createRoute({
    getParentRoute: () => problemSetProblemRoute,
    path: '/',
    component: lazyRouteComponent(
      () => import('./problemsets/single/problems/single/statement/ProblemStatementPage/ProblemStatementPage')
    ),
  });

  const problemSetProblemSubmissionsRoute = createRoute({
    getParentRoute: () => problemSetProblemRoute,
    path: 'submissions',
    component: lazyRouteComponent(
      () => import('./problemsets/single/problems/single/submissions/ProblemSubmissionLayout')
    ),
  });

  const problemSetProblemSubmissionsIndexRoute = createRoute({
    getParentRoute: () => problemSetProblemSubmissionsRoute,
    path: '/',
    component: lazyRouteComponent(
      () => import('./problemsets/single/problems/single/submissions/ProblemSubmissionsPage/ProblemSubmissionsPage')
    ),
  });

  const problemSetProblemSubmissionsMineRoute = createRoute({
    getParentRoute: () => problemSetProblemSubmissionsRoute,
    path: 'mine',
    component: lazyRouteComponent(
      () => import('./problemsets/single/problems/single/submissions/ProblemSubmissionsPage/ProblemSubmissionsPage')
    ),
  });

  const problemSetProblemSubmissionRoute = createRoute({
    getParentRoute: () => problemSetProblemSubmissionsRoute,
    path: '$submissionId',
    component: lazyRouteComponent(
      () =>
        import('./problemsets/single/problems/single/submissions/single/ProblemSubmissionPage/ProblemSubmissionPage')
    ),
  });

  const problemSetProblemResultsRoute = createRoute({
    getParentRoute: () => problemSetProblemRoute,
    path: 'results',
    component: lazyRouteComponent(() => import('./problemsets/single/problems/single/results/ProblemResultsLayout')),
  });

  const problemSetProblemResultsIndexRoute = createRoute({
    getParentRoute: () => problemSetProblemResultsRoute,
    path: '/',
    component: lazyRouteComponent(
      () =>
        import('./problemsets/single/problems/single/results/ProblemSubmissionSummaryPage/ProblemSubmissionSummaryPage')
    ),
  });

  const problemSetProblemResultsAllRoute = createRoute({
    getParentRoute: () => problemSetProblemResultsRoute,
    path: 'all',
    component: lazyRouteComponent(
      () => import('./problemsets/single/problems/single/results/ProblemSubmissionsPage/ProblemSubmissionsPage')
    ),
  });

  const problemSetProblemResultsUserRoute = createRoute({
    getParentRoute: () => problemSetProblemResultsRoute,
    path: 'users/$username',
    component: lazyRouteComponent(
      () =>
        import('./problemsets/single/problems/single/results/ProblemSubmissionSummaryPage/ProblemSubmissionSummaryPage')
    ),
  });

  return problemsRoute.addChildren([
    problemsIndexRoute.addChildren([problemsProblemsRoute, problemSetsRoute]),
    problemSetRoute.addChildren([problemSetIndexRoute]),
    problemSetProblemRoute.addChildren([
      problemSetProblemIndexRoute,
      problemSetProblemSubmissionsRoute.addChildren([
        problemSetProblemSubmissionsIndexRoute,
        problemSetProblemSubmissionsMineRoute,
        problemSetProblemSubmissionRoute,
      ]),
      problemSetProblemResultsRoute.addChildren([
        problemSetProblemResultsIndexRoute,
        problemSetProblemResultsAllRoute,
        problemSetProblemResultsUserRoute,
      ]),
    ]),
  ]);
};
