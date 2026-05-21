import { Outlet, createRoute, lazyRouteComponent, redirect } from '@tanstack/react-router';

import { retryImport } from '../../lazy';
import { ProblemType } from '../../modules/api/sandalphon/problem';
import { archivesQueryOptions } from '../../modules/queries/archive';
import { problemTagsQueryOptions, problemsQueryOptions } from '../../modules/queries/problem';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemQueryOptions,
  problemSetProblemReportQueryOptions,
  problemSetProblemWorksheetQueryOptions,
  problemSetProblemsQueryOptions,
  problemSetsQueryOptions,
} from '../../modules/queries/problemSet';
import {
  problemSetBundleLatestSubmissionsQueryOptions,
  problemSetBundleSubmissionSummaryQueryOptions,
  problemSetBundleSubmissionsQueryOptions,
} from '../../modules/queries/problemSetSubmissionBundle';
import { problemSetProgrammingSubmissionsQueryOptions } from '../../modules/queries/problemSetSubmissionProgramming';
import { submissionWithSourceQueryOptions } from '../../modules/queries/submissionProgramming';
import { queryClient } from '../../modules/queryClient';
import { getUser } from '../../modules/session';
import { getWebPrefs } from '../../modules/webPrefs';
import { createDocumentTitle } from '../../utils/title';
import { isUserBlocked } from '../blockedUsernames';

export const createProblemsRoutes = appRoute => {
  const problemsRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'problems',
    component: Outlet,
    head: () => ({ meta: [{ title: createDocumentTitle('Problems') }] }),
    beforeLoad: () => {
      if (isUserBlocked(getUser())) {
        throw redirect({ to: '/' });
      }
    },
  });

  const problemsIndexRoute = createRoute({
    getParentRoute: () => problemsRoute,
    id: 'problems-index',
    component: lazyRouteComponent(retryImport(() => import('./ProblemsIndexLayout'))),
  });

  const problemsProblemsRoute = createRoute({
    getParentRoute: () => problemsIndexRoute,
    path: '/',
    component: lazyRouteComponent(retryImport(() => import('./problems/ProblemsPage/ProblemsPage'))),
    loader: ({ search = {} }) => {
      queryClient.prefetchQuery(problemsQueryOptions({ tags: search.tags, page: search.page }));
      queryClient.prefetchQuery(problemTagsQueryOptions());
    },
  });

  const problemSetsRoute = createRoute({
    getParentRoute: () => problemsIndexRoute,
    path: 'problemsets',
    component: lazyRouteComponent(retryImport(() => import('./problemsets/ProblemSetsPage/ProblemSetsPage'))),
    loader: ({ search = {} }) => {
      queryClient.prefetchQuery(
        problemSetsQueryOptions({ archiveSlug: search.archive, name: search.name, page: search.page })
      );
      queryClient.prefetchQuery(archivesQueryOptions());
    },
  });

  const problemSetRoute = createRoute({
    getParentRoute: () => problemsRoute,
    path: '$problemSetSlug',
    component: lazyRouteComponent(retryImport(() => import('./problemsets/single/SingleProblemSetLayout'))),
    loader: async ({ params: { problemSetSlug } }) => {
      await queryClient.ensureQueryData(problemSetBySlugQueryOptions(problemSetSlug));
    },
  });

  const problemSetIndexRoute = createRoute({
    getParentRoute: () => problemSetRoute,
    path: '/',
    component: lazyRouteComponent(
      retryImport(() => import('./problemsets/single/problems/ProblemSetProblemsPage/ProblemSetProblemsPage'))
    ),
    loader: async ({ params: { problemSetSlug } }) => {
      const problemSet = await queryClient.ensureQueryData(problemSetBySlugQueryOptions(problemSetSlug));
      queryClient.prefetchQuery(problemSetProblemsQueryOptions(problemSet.jid));
    },
  });

  const problemSetProblemRoute = createRoute({
    getParentRoute: () => problemsRoute,
    path: '$problemSetSlug/$problemAlias',
    component: lazyRouteComponent(
      retryImport(() => import('./problemsets/single/problems/single/SingleProblemSetProblemLayout'))
    ),
    loader: async ({ params: { problemSetSlug, problemAlias } }) => {
      const problemSet = await queryClient.ensureQueryData(problemSetBySlugQueryOptions(problemSetSlug));
      await queryClient.ensureQueryData(problemSetProblemQueryOptions(problemSet.jid, problemAlias));
    },
  });

  const problemSetProblemIndexRoute = createRoute({
    getParentRoute: () => problemSetProblemRoute,
    path: '/',
    component: lazyRouteComponent(
      retryImport(
        () => import('./problemsets/single/problems/single/statement/ProblemStatementPage/ProblemStatementPage')
      )
    ),
    loader: async ({ params: { problemSetSlug, problemAlias } }) => {
      const problemSet = await queryClient.ensureQueryData(problemSetBySlugQueryOptions(problemSetSlug));
      const language = getWebPrefs().statementLanguage;
      queryClient
        .fetchQuery(problemSetProblemWorksheetQueryOptions(problemSet.jid, problemAlias, { language }))
        .then(worksheet => {
          if (worksheet?.problem?.type === ProblemType.Bundle) {
            queryClient.prefetchQuery(
              problemSetBundleLatestSubmissionsQueryOptions(problemSet.jid, worksheet.problem.alias)
            );
          }
        });
    },
  });

  const problemSetProblemSubmissionsRoute = createRoute({
    getParentRoute: () => problemSetProblemRoute,
    path: 'submissions',
    component: Outlet,
    head: () => ({ meta: [{ title: createDocumentTitle('Submissions') }] }),
  });

  const problemSetProblemSubmissionsIndexRoute = createRoute({
    getParentRoute: () => problemSetProblemSubmissionsRoute,
    path: '/',
    component: lazyRouteComponent(
      retryImport(
        () => import('./problemsets/single/problems/single/submissions/ProblemSubmissionsPage/ProblemSubmissionsPage')
      )
    ),
    loader: async ({ params: { problemSetSlug, problemAlias }, search = {} }) => {
      const problemSet = await queryClient.ensureQueryData(problemSetBySlugQueryOptions(problemSetSlug));
      const problem = await queryClient.ensureQueryData(problemSetProblemQueryOptions(problemSet.jid, problemAlias));
      queryClient.prefetchQuery(
        problemSetProgrammingSubmissionsQueryOptions(problem.problemJid, {
          beforeId: search.before,
          afterId: search.after,
        })
      );
    },
  });

  const problemSetProblemSubmissionsMineRoute = createRoute({
    getParentRoute: () => problemSetProblemSubmissionsRoute,
    path: 'mine',
    component: lazyRouteComponent(
      retryImport(
        () => import('./problemsets/single/problems/single/submissions/ProblemSubmissionsPage/ProblemSubmissionsPage')
      )
    ),
    loader: async ({ params: { problemSetSlug, problemAlias } }) => {
      const problemSet = await queryClient.ensureQueryData(problemSetBySlugQueryOptions(problemSetSlug));
      const problem = await queryClient.ensureQueryData(problemSetProblemQueryOptions(problemSet.jid, problemAlias));
      const username = getUser()?.username;
      queryClient.prefetchQuery(problemSetProgrammingSubmissionsQueryOptions(problem.problemJid, { username }));
    },
  });

  const problemSetProblemSubmissionRoute = createRoute({
    getParentRoute: () => problemSetProblemSubmissionsRoute,
    path: '$submissionId',
    component: lazyRouteComponent(
      retryImport(
        () =>
          import('./problemsets/single/problems/single/submissions/single/ProblemSubmissionPage/ProblemSubmissionPage')
      )
    ),
    loader: ({ params: { submissionId } }) => {
      const language = getWebPrefs().statementLanguage;
      queryClient.prefetchQuery(submissionWithSourceQueryOptions(+submissionId, { language }));
    },
  });

  const problemSetProblemResultsRoute = createRoute({
    getParentRoute: () => problemSetProblemRoute,
    path: 'results',
    component: Outlet,
    head: () => ({ meta: [{ title: createDocumentTitle('Results') }] }),
  });

  const problemSetProblemResultsIndexRoute = createRoute({
    getParentRoute: () => problemSetProblemResultsRoute,
    path: '/',
    component: lazyRouteComponent(
      retryImport(
        () =>
          import(
            './problemsets/single/problems/single/results/ProblemSubmissionSummaryPage/ProblemSubmissionSummaryPage'
          )
      )
    ),
    loader: async ({ params: { problemSetSlug, problemAlias } }) => {
      const problemSet = await queryClient.ensureQueryData(problemSetBySlugQueryOptions(problemSetSlug));
      const problem = await queryClient.ensureQueryData(problemSetProblemQueryOptions(problemSet.jid, problemAlias));
      const language = getWebPrefs().statementLanguage;
      queryClient.prefetchQuery(problemSetProblemReportQueryOptions(problemSet.jid, problemAlias));
      queryClient.prefetchQuery(
        problemSetBundleSubmissionSummaryQueryOptions(problemSet.jid, { problemJid: problem.problemJid, language })
      );
    },
  });

  const problemSetProblemResultsAllRoute = createRoute({
    getParentRoute: () => problemSetProblemResultsRoute,
    path: 'all',
    component: lazyRouteComponent(
      retryImport(
        () => import('./problemsets/single/problems/single/results/ProblemSubmissionsPage/ProblemSubmissionsPage')
      )
    ),
    loader: async ({ params: { problemSetSlug, problemAlias } }) => {
      const problemSet = await queryClient.ensureQueryData(problemSetBySlugQueryOptions(problemSetSlug));
      const problem = await queryClient.ensureQueryData(problemSetProblemQueryOptions(problemSet.jid, problemAlias));
      queryClient.prefetchQuery(
        problemSetBundleSubmissionsQueryOptions(problemSet.jid, { problemAlias: problem.alias })
      );
    },
  });

  const problemSetProblemResultsUserRoute = createRoute({
    getParentRoute: () => problemSetProblemResultsRoute,
    path: 'users/$username',
    component: lazyRouteComponent(
      retryImport(
        () =>
          import(
            './problemsets/single/problems/single/results/ProblemSubmissionSummaryPage/ProblemSubmissionSummaryPage'
          )
      )
    ),
    loader: async ({ params: { problemSetSlug, problemAlias, username } }) => {
      const problemSet = await queryClient.ensureQueryData(problemSetBySlugQueryOptions(problemSetSlug));
      const problem = await queryClient.ensureQueryData(problemSetProblemQueryOptions(problemSet.jid, problemAlias));
      const language = getWebPrefs().statementLanguage;
      queryClient.prefetchQuery(
        problemSetBundleSubmissionSummaryQueryOptions(problemSet.jid, {
          problemJid: problem.problemJid,
          username,
          language,
        })
      );
    },
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
