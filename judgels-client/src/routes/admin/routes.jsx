import { createRoute, lazyRouteComponent, redirect } from '@tanstack/react-router';

import { retryImport } from '../../lazy';
import { archiveBySlugQueryOptions } from '../../modules/queries/archive';
import { archivesQueryOptions } from '../../modules/queries/archive';
import { chapterByJidQueryOptions } from '../../modules/queries/chapter';
import { chapterLessonsQueryOptions } from '../../modules/queries/chapterLesson';
import { chapterProblemsQueryOptions } from '../../modules/queries/chapterProblem';
import { courseBySlugQueryOptions } from '../../modules/queries/course';
import { courseChaptersQueryOptions } from '../../modules/queries/courseChapter';
import { curriculumByJidQueryOptions } from '../../modules/queries/curriculum';
import { problemQueryOptions } from '../../modules/queries/problem';
import {
  problemGradingConfigQueryOptions,
  problemGradingHelperFilesQueryOptions,
  problemGradingLanguageRestrictionQueryOptions,
  problemGradingTestDataFilesQueryOptions,
} from '../../modules/queries/problemGrading';
import { problemSetBySlugQueryOptions } from '../../modules/queries/problemSet';
import { problemSetProblemsQueryOptions } from '../../modules/queries/problemSetProblem';
import {
  problemStatementLanguagesQueryOptions,
  problemStatementMediaFilesQueryOptions,
} from '../../modules/queries/problemStatement';
import { userByUsernameQueryOptions } from '../../modules/queries/user';
import { userInfoQueryOptions } from '../../modules/queries/userInfo';
import { queryClient } from '../../modules/queryClient';
import { createDocumentTitle } from '../../utils/title';

export const createAdminRoutes = appRoute => {
  const adminRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'admin',
    component: lazyRouteComponent(retryImport(() => import('./AdminLayout'))),
    head: () => ({ meta: [{ title: createDocumentTitle('Admin') }] }),
  });

  const adminIndexRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: '/',
    component: lazyRouteComponent(retryImport(() => import('./AdminIndexPage'))),
  });

  const adminUsersRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'users',
    component: lazyRouteComponent(retryImport(() => import('./users/UsersPage/UsersPage'))),
  });

  const adminUserRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'users/$username',
    component: lazyRouteComponent(retryImport(() => import('./users/UserPage/UserPage'))),
    loader: async ({ params: { username } }) => {
      const user = await queryClient.ensureQueryData(userByUsernameQueryOptions(username));
      await queryClient.ensureQueryData(userInfoQueryOptions(user.jid));
    },
  });

  const adminRolesRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'roles',
    component: lazyRouteComponent(retryImport(() => import('./roles/RolesPage/RolesPage'))),
  });

  const adminRatingsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'ratings',
    component: lazyRouteComponent(retryImport(() => import('./ratings/RatingsPage/RatingsPage'))),
  });

  const adminContestsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'contests',
    component: lazyRouteComponent(retryImport(() => import('./contests/ContestsPage/ContestsPage'))),
  });

  const adminCurriculumsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'curriculums',
    component: lazyRouteComponent(retryImport(() => import('./curriculums/CurriculumsPage/CurriculumsPage'))),
  });

  const adminCurriculumRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'curriculums/$curriculumJid',
    component: lazyRouteComponent(retryImport(() => import('./curriculums/CurriculumPage/CurriculumPage'))),
    loader: async ({ params: { curriculumJid } }) => {
      await queryClient.ensureQueryData(curriculumByJidQueryOptions(curriculumJid));
    },
  });

  const adminCoursesRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'courses',
    component: lazyRouteComponent(retryImport(() => import('./courses/CoursesPage/CoursesPage'))),
  });

  const adminCourseRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'courses/$courseSlug',
    component: lazyRouteComponent(retryImport(() => import('./courses/CoursePage/CoursePage'))),
    loader: async ({ params: { courseSlug } }) => {
      const course = await queryClient.ensureQueryData(courseBySlugQueryOptions(courseSlug));
      await queryClient.ensureQueryData(courseChaptersQueryOptions(course.jid));
    },
  });

  const adminChaptersRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'chapters',
    component: lazyRouteComponent(retryImport(() => import('./chapters/ChaptersPage/ChaptersPage'))),
  });

  const adminChapterRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'chapters/$chapterJid',
    component: lazyRouteComponent(retryImport(() => import('./chapters/ChapterPage/ChapterPage'))),
    loader: async ({ params: { chapterJid } }) => {
      await queryClient.ensureQueryData(chapterByJidQueryOptions(chapterJid));
      await Promise.all([
        queryClient.ensureQueryData(chapterLessonsQueryOptions(chapterJid)),
        queryClient.ensureQueryData(chapterProblemsQueryOptions(chapterJid)),
      ]);
    },
  });

  const adminArchivesRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'archives',
    component: lazyRouteComponent(retryImport(() => import('./archives/ArchivesPage/ArchivesPage'))),
  });

  const adminArchiveRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'archives/$archiveSlug',
    component: lazyRouteComponent(retryImport(() => import('./archives/ArchivePage/ArchivePage'))),
    loader: async ({ params: { archiveSlug } }) => {
      await queryClient.ensureQueryData(archiveBySlugQueryOptions(archiveSlug));
    },
  });

  const adminProblemSetsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'problemsets',
    component: lazyRouteComponent(retryImport(() => import('./problemsets/ProblemSetsPage/ProblemSetsPage'))),
  });

  const adminProblemSetRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'problemsets/$problemSetSlug',
    component: lazyRouteComponent(retryImport(() => import('./problemsets/ProblemSetPage/ProblemSetPage'))),
    loader: async ({ params: { problemSetSlug } }) => {
      const problemSet = await queryClient.ensureQueryData(problemSetBySlugQueryOptions(problemSetSlug));
      await Promise.all([
        queryClient.ensureQueryData(archivesQueryOptions()),
        queryClient.ensureQueryData(problemSetProblemsQueryOptions(problemSet.jid)),
      ]);
    },
  });

  const adminProblemsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'problems',
    component: lazyRouteComponent(retryImport(() => import('./problems/ProblemsPage/ProblemsPage'))),
  });

  const adminProblemRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'problems/$problemJid',
    component: lazyRouteComponent(retryImport(() => import('./problems/ProblemLayout/ProblemLayout'))),
    loader: async ({ params: { problemJid } }) => {
      await queryClient.ensureQueryData(problemQueryOptions(problemJid));
    },
  });

  const adminProblemIndexRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: '/',
    beforeLoad: ({ params }) => {
      throw redirect({ to: '/admin/problems/$problemJid/general', params, replace: true });
    },
  });

  const adminProblemGeneralRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'general',
    component: lazyRouteComponent(retryImport(() => import('./problems/ProblemGeneralPage/ProblemGeneralPage'))),
  });

  const adminProblemStatementRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'statements',
    component: lazyRouteComponent(retryImport(() => import('./problems/ProblemStatementPage/ProblemStatementPage'))),
    loader: async ({ params: { problemJid } }) => {
      await queryClient.ensureQueryData(problemStatementLanguagesQueryOptions(problemJid));
    },
  });

  const adminProblemStatementLanguagesRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'languages',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/ProblemStatementLanguagesPage/ProblemStatementLanguagesPage'))
    ),
    loader: async ({ params: { problemJid } }) => {
      await queryClient.ensureQueryData(problemStatementLanguagesQueryOptions(problemJid));
    },
  });

  const adminProblemStatementMediaRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'media',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/ProblemStatementMediaPage/ProblemStatementMediaPage'))
    ),
    loader: async ({ params: { problemJid } }) => {
      await queryClient.ensureQueryData(problemStatementMediaFilesQueryOptions(problemJid));
    },
  });

  const adminProblemGradingRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'grading',
    component: lazyRouteComponent(retryImport(() => import('./problems/ProblemGradingLayout/ProblemGradingLayout'))),
  });

  const adminProblemGradingIndexRoute = createRoute({
    getParentRoute: () => adminProblemGradingRoute,
    path: '/',
    beforeLoad: ({ params }) => {
      throw redirect({ to: '/admin/problems/$problemJid/grading/engine', params, replace: true });
    },
  });

  const adminProblemGradingEngineRoute = createRoute({
    getParentRoute: () => adminProblemGradingRoute,
    path: 'engine',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/ProblemGradingEnginePage/ProblemGradingEnginePage'))
    ),
    loader: async ({ params: { problemJid } }) => {
      await queryClient.ensureQueryData(problemGradingConfigQueryOptions(problemJid));
    },
  });

  const adminProblemGradingConfigRoute = createRoute({
    getParentRoute: () => adminProblemGradingRoute,
    path: 'config',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/ProblemGradingConfigPage/ProblemGradingConfigPage'))
    ),
    loader: async ({ params: { problemJid } }) => {
      await Promise.all([
        queryClient.ensureQueryData(problemGradingConfigQueryOptions(problemJid)),
        queryClient.ensureQueryData(problemGradingTestDataFilesQueryOptions(problemJid)),
        queryClient.ensureQueryData(problemGradingHelperFilesQueryOptions(problemJid)),
      ]);
    },
  });

  const adminProblemGradingTestDataRoute = createRoute({
    getParentRoute: () => adminProblemGradingRoute,
    path: 'test-data',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/ProblemGradingTestDataPage/ProblemGradingTestDataPage'))
    ),
    loader: async ({ params: { problemJid } }) => {
      await queryClient.ensureQueryData(problemGradingTestDataFilesQueryOptions(problemJid));
    },
  });

  const adminProblemGradingHelpersRoute = createRoute({
    getParentRoute: () => adminProblemGradingRoute,
    path: 'helpers',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/ProblemGradingHelpersPage/ProblemGradingHelpersPage'))
    ),
    loader: async ({ params: { problemJid } }) => {
      await queryClient.ensureQueryData(problemGradingHelperFilesQueryOptions(problemJid));
    },
  });

  const adminProblemGradingLanguageRestrictionRoute = createRoute({
    getParentRoute: () => adminProblemGradingRoute,
    path: 'languages',
    component: lazyRouteComponent(
      retryImport(
        () => import('./problems/ProblemGradingLanguageRestrictionPage/ProblemGradingLanguageRestrictionPage')
      )
    ),
    loader: async ({ params: { problemJid } }) => {
      await queryClient.ensureQueryData(problemGradingLanguageRestrictionQueryOptions(problemJid));
    },
  });

  const adminLessonsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'lessons',
    component: lazyRouteComponent(retryImport(() => import('./lessons/LessonsPage/LessonsPage'))),
  });

  const adminSettingsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'settings',
    component: lazyRouteComponent(retryImport(() => import('./settings/SettingsPage/SettingsPage'))),
  });

  return adminRoute.addChildren([
    adminIndexRoute,
    adminUsersRoute,
    adminUserRoute,
    adminRolesRoute,
    adminRatingsRoute,
    adminContestsRoute,
    adminCurriculumsRoute,
    adminCurriculumRoute,
    adminCoursesRoute,
    adminCourseRoute,
    adminChaptersRoute,
    adminChapterRoute,
    adminArchivesRoute,
    adminArchiveRoute,
    adminProblemSetsRoute,
    adminProblemSetRoute,
    adminProblemsRoute,
    adminProblemRoute.addChildren([
      adminProblemIndexRoute,
      adminProblemGeneralRoute,
      adminProblemStatementRoute,
      adminProblemStatementLanguagesRoute,
      adminProblemStatementMediaRoute,
      adminProblemGradingRoute.addChildren([
        adminProblemGradingIndexRoute,
        adminProblemGradingEngineRoute,
        adminProblemGradingConfigRoute,
        adminProblemGradingTestDataRoute,
        adminProblemGradingHelpersRoute,
        adminProblemGradingLanguageRestrictionRoute,
      ]),
    ]),
    adminLessonsRoute,
    adminSettingsRoute,
  ]);
};
