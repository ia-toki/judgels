import { Outlet, createRoute, lazyRouteComponent } from '@tanstack/react-router';

import { retryImport } from '../../lazy';
import { ContestStyle } from '../../modules/api/uriel/contest';
import {
  contestBySlugQueryOptions,
  contestDescriptionQueryOptions,
  contestsQueryOptions,
} from '../../modules/queries/contest';
import { contestAnnouncementsQueryOptions } from '../../modules/queries/contestAnnouncement';
import { contestContestantsQueryOptions } from '../../modules/queries/contestContestant';
import { contestFilesQueryOptions } from '../../modules/queries/contestFile';
import { contestLogsQueryOptions } from '../../modules/queries/contestLog';
import { contestManagersQueryOptions } from '../../modules/queries/contestManager';
import {
  contestBundleProblemWorksheetQueryOptions,
  contestProblemsQueryOptions,
  contestProgrammingProblemWorksheetQueryOptions,
} from '../../modules/queries/contestProblem';
import { contestScoreboardQueryOptions } from '../../modules/queries/contestScoreboard';
import { contestProgrammingSubmissionsQueryOptions } from '../../modules/queries/contestSubmissionProgramming';
import { contestSupervisorsQueryOptions } from '../../modules/queries/contestSupervisor';
import { contestWebConfigQueryOptions } from '../../modules/queries/contestWeb';
import { queryClient } from '../../modules/queryClient';
import { getWebPrefs } from '../../modules/webPrefs';
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
    loader: ({ search = {} }) => {
      queryClient.prefetchQuery(contestsQueryOptions({ name: search.name, page: search.page }));
    },
  });

  const contestRoute = createRoute({
    getParentRoute: () => contestsRoute,
    path: '$contestSlug',
    component: lazyRouteComponent(retryImport(() => import('./contests/single/SingleContestLayout'))),
    loader: async ({ params: { contestSlug } }) => {
      const contest = await queryClient.ensureQueryData(contestBySlugQueryOptions(contestSlug));
      await queryClient.ensureQueryData(contestWebConfigQueryOptions(contest.jid));
    },
  });

  const contestOverviewRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: '/',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/overview/ContestOverviewPage/ContestOverviewPage'))
    ),
    loader: async ({ params: { contestSlug } }) => {
      const contest = await queryClient.ensureQueryData(contestBySlugQueryOptions(contestSlug));
      queryClient.prefetchQuery(contestDescriptionQueryOptions(contest.jid));
    },
  });

  const contestAnnouncementsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'announcements',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/announcements/ContestAnnouncementsPage/ContestAnnouncementsPage'))
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Announcements') }] }),
    loader: async ({ params: { contestSlug }, search = {} }) => {
      const contest = await queryClient.ensureQueryData(contestBySlugQueryOptions(contestSlug));
      queryClient.prefetchQuery(contestAnnouncementsQueryOptions(contest.jid, { page: search.page }));
    },
  });

  const contestProblemsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'problems',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/problems/ContestProblemsPage/ContestProblemsPage'))
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Problems') }] }),
    loader: async ({ params: { contestSlug } }) => {
      const contest = await queryClient.ensureQueryData(contestBySlugQueryOptions(contestSlug));
      queryClient.prefetchQuery(contestProblemsQueryOptions(contest.jid));
    },
  });

  const contestProblemRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'problems/$problemAlias',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/problems/single/ContestProblemPage/ContestProblemPage'))
    ),
    loader: async ({ params: { contestSlug, problemAlias } }) => {
      const contest = await queryClient.ensureQueryData(contestBySlugQueryOptions(contestSlug));
      const language = getWebPrefs().statementLanguage;
      const worksheetQueryOptions =
        contest.style === ContestStyle.Bundle
          ? contestBundleProblemWorksheetQueryOptions
          : contestProgrammingProblemWorksheetQueryOptions;
      queryClient.prefetchQuery(worksheetQueryOptions(contest.jid, problemAlias, { language }));
    },
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
    loader: async ({ params: { contestSlug }, search = {} }) => {
      const contest = await queryClient.ensureQueryData(contestBySlugQueryOptions(contestSlug));
      queryClient.prefetchQuery(contestContestantsQueryOptions(contest.jid, { page: search.page }));
    },
  });

  const contestSupervisorsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'supervisors',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/supervisors/ContestSupervisorsPage/ContestSupervisorsPage'))
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Supervisors') }] }),
    loader: async ({ params: { contestSlug }, search = {} }) => {
      const contest = await queryClient.ensureQueryData(contestBySlugQueryOptions(contestSlug));
      queryClient.prefetchQuery(contestSupervisorsQueryOptions(contest.jid, { page: search.page }));
    },
  });

  const contestManagersRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'managers',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/managers/ContestManagersPage/ContestManagersPage'))
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Managers') }] }),
    loader: async ({ params: { contestSlug }, search = {} }) => {
      const contest = await queryClient.ensureQueryData(contestBySlugQueryOptions(contestSlug));
      queryClient.prefetchQuery(contestManagersQueryOptions(contest.jid, { page: search.page }));
    },
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
    loader: async ({ params: { contestSlug }, search = {} }) => {
      const contest = await queryClient.ensureQueryData(contestBySlugQueryOptions(contestSlug));
      queryClient.prefetchQuery(
        contestProgrammingSubmissionsQueryOptions(contest.jid, {
          username: search.username,
          problemAlias: search.problemAlias,
          page: search.page,
        })
      );
    },
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
    loader: async ({ params: { contestSlug }, search = {} }) => {
      const contest = await queryClient.ensureQueryData(contestBySlugQueryOptions(contestSlug));
      queryClient.prefetchQuery(
        contestScoreboardQueryOptions(contest.jid, {
          frozen: !!search.frozen,
          showClosedProblems: !!search.showClosedProblems,
          page: search.page,
        })
      );
    },
  });

  const contestFilesRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'files',
    component: lazyRouteComponent(
      retryImport(() => import('./contests/single/files/ContestFilesPage/ContestFilesPage'))
    ),
    head: () => ({ meta: [{ title: createDocumentTitle('Files') }] }),
    loader: async ({ params: { contestSlug } }) => {
      const contest = await queryClient.ensureQueryData(contestBySlugQueryOptions(contestSlug));
      queryClient.prefetchQuery(contestFilesQueryOptions(contest.jid));
    },
  });

  const contestLogsRoute = createRoute({
    getParentRoute: () => contestRoute,
    path: 'logs',
    component: lazyRouteComponent(retryImport(() => import('./contests/single/logs/ContestLogsPage/ContestLogsPage'))),
    head: () => ({ meta: [{ title: createDocumentTitle('Logs') }] }),
    loader: async ({ params: { contestSlug }, search = {} }) => {
      const contest = await queryClient.ensureQueryData(contestBySlugQueryOptions(contestSlug));
      queryClient.prefetchQuery(
        contestLogsQueryOptions(contest.jid, {
          username: search.username,
          problemAlias: search.problemAlias,
          page: search.page,
        })
      );
    },
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
