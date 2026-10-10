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
import { lessonBySlugQueryOptions, lessonQueryOptions } from '../../modules/queries/lesson';
import {
  lessonStatementLanguagesQueryOptions,
  lessonStatementMediaFilesQueryOptions,
} from '../../modules/queries/lessonStatement';
import { lessonVersionsQueryOptions } from '../../modules/queries/lessonVersion';
import { problemBySlugQueryOptions, problemQueryOptions } from '../../modules/queries/problem';
import {
  problemEditorialLanguagesQueryOptions,
  problemEditorialMediaFilesQueryOptions,
} from '../../modules/queries/problemEditorial';
import {
  problemGradingConfigQueryOptions,
  problemGradingHelperFilesQueryOptions,
  problemGradingLanguageRestrictionQueryOptions,
  problemGradingTestDataFilesQueryOptions,
} from '../../modules/queries/problemGrading';
import { problemItemsQueryOptions } from '../../modules/queries/problemItem';
import { problemSetBySlugQueryOptions } from '../../modules/queries/problemSet';
import { problemSetProblemsQueryOptions } from '../../modules/queries/problemSetProblem';
import {
  problemStatementLanguagesQueryOptions,
  problemStatementMediaFilesQueryOptions,
} from '../../modules/queries/problemStatement';
import { problemVersionsQueryOptions } from '../../modules/queries/problemVersion';
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
    path: 'problems/$problemSlug',
    component: lazyRouteComponent(retryImport(() => import('./problems/ProblemLayout/ProblemLayout'))),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemQueryOptions(problem.jid));
    },
  });

  const adminProblemIndexRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: '/',
    beforeLoad: ({ params }) => {
      throw redirect({ to: '/admin/problems/$problemSlug/general', params, replace: true });
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
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemStatementLanguagesQueryOptions(problem.jid));
    },
  });

  const adminProblemStatementLanguagesRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'languages',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/ProblemStatementLanguagesPage/ProblemStatementLanguagesPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemStatementLanguagesQueryOptions(problem.jid));
    },
  });

  const adminProblemStatementMediaRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'media',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/ProblemStatementMediaPage/ProblemStatementMediaPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemStatementMediaFilesQueryOptions(problem.jid));
    },
  });

  // A problem without an editorial has nothing to load: its layout offers to create one instead.
  const ensureProblemEditorialQueryData = async (problemJid, editorialQueryOptions) => {
    const { hasEditorial } = await queryClient.ensureQueryData(problemQueryOptions(problemJid));
    if (hasEditorial) {
      await queryClient.ensureQueryData(editorialQueryOptions(problemJid));
    }
  };

  const adminProblemEditorialLayoutRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'editorial',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/ProblemEditorialLayout/ProblemEditorialLayout'))
    ),
  });

  const adminProblemEditorialIndexRoute = createRoute({
    getParentRoute: () => adminProblemEditorialLayoutRoute,
    path: '/',
    beforeLoad: ({ params }) => {
      throw redirect({ to: '/admin/problems/$problemSlug/editorial/content', params, replace: true });
    },
  });

  const adminProblemEditorialRoute = createRoute({
    getParentRoute: () => adminProblemEditorialLayoutRoute,
    path: 'content',
    component: lazyRouteComponent(retryImport(() => import('./problems/ProblemEditorialPage/ProblemEditorialPage'))),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await ensureProblemEditorialQueryData(problem.jid, problemEditorialLanguagesQueryOptions);
    },
  });

  const adminProblemEditorialLanguagesRoute = createRoute({
    getParentRoute: () => adminProblemEditorialLayoutRoute,
    path: 'languages',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/ProblemEditorialLanguagesPage/ProblemEditorialLanguagesPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await ensureProblemEditorialQueryData(problem.jid, problemEditorialLanguagesQueryOptions);
    },
  });

  const adminProblemEditorialMediaRoute = createRoute({
    getParentRoute: () => adminProblemEditorialLayoutRoute,
    path: 'media',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/ProblemEditorialMediaPage/ProblemEditorialMediaPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await ensureProblemEditorialQueryData(problem.jid, problemEditorialMediaFilesQueryOptions);
    },
  });

  const adminProblemItemsRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'items',
    component: lazyRouteComponent(retryImport(() => import('./problems/ProblemItemsPage/ProblemItemsPage'))),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemItemsQueryOptions(problem.jid));
    },
  });

  const adminProblemItemRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'items/$itemJid',
    component: lazyRouteComponent(retryImport(() => import('./problems/ProblemItemPage/ProblemItemPage'))),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemStatementLanguagesQueryOptions(problem.jid));
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
      throw redirect({ to: '/admin/problems/$problemSlug/grading/engine', params, replace: true });
    },
  });

  const adminProblemGradingEngineRoute = createRoute({
    getParentRoute: () => adminProblemGradingRoute,
    path: 'engine',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/ProblemGradingEnginePage/ProblemGradingEnginePage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemGradingConfigQueryOptions(problem.jid));
    },
  });

  const adminProblemGradingConfigRoute = createRoute({
    getParentRoute: () => adminProblemGradingRoute,
    path: 'config',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/ProblemGradingConfigPage/ProblemGradingConfigPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await Promise.all([
        queryClient.ensureQueryData(problemGradingConfigQueryOptions(problem.jid)),
        queryClient.ensureQueryData(problemGradingTestDataFilesQueryOptions(problem.jid)),
        queryClient.ensureQueryData(problemGradingHelperFilesQueryOptions(problem.jid)),
      ]);
    },
  });

  const adminProblemGradingTestDataRoute = createRoute({
    getParentRoute: () => adminProblemGradingRoute,
    path: 'test-data',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/ProblemGradingTestDataPage/ProblemGradingTestDataPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemGradingTestDataFilesQueryOptions(problem.jid));
    },
  });

  const adminProblemGradingHelpersRoute = createRoute({
    getParentRoute: () => adminProblemGradingRoute,
    path: 'helpers',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/ProblemGradingHelpersPage/ProblemGradingHelpersPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemGradingHelperFilesQueryOptions(problem.jid));
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
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemGradingLanguageRestrictionQueryOptions(problem.jid));
    },
  });

  const adminProblemVersionsRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'versions',
    component: lazyRouteComponent(retryImport(() => import('./problems/ProblemVersionsPage/ProblemVersionsPage'))),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemVersionsQueryOptions(problem.jid));
    },
  });

  const adminLessonsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'lessons',
    component: lazyRouteComponent(retryImport(() => import('./lessons/LessonsPage/LessonsPage'))),
  });

  const adminLessonRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'lessons/$lessonSlug',
    component: lazyRouteComponent(retryImport(() => import('./lessons/LessonLayout/LessonLayout'))),
    loader: async ({ params: { lessonSlug } }) => {
      const lesson = await queryClient.ensureQueryData(lessonBySlugQueryOptions(lessonSlug));
      await queryClient.ensureQueryData(lessonQueryOptions(lesson.jid));
    },
  });

  const adminLessonIndexRoute = createRoute({
    getParentRoute: () => adminLessonRoute,
    path: '/',
    beforeLoad: ({ params }) => {
      throw redirect({ to: '/admin/lessons/$lessonSlug/general', params, replace: true });
    },
  });

  const adminLessonGeneralRoute = createRoute({
    getParentRoute: () => adminLessonRoute,
    path: 'general',
    component: lazyRouteComponent(retryImport(() => import('./lessons/LessonGeneralPage/LessonGeneralPage'))),
  });

  const adminLessonStatementRoute = createRoute({
    getParentRoute: () => adminLessonRoute,
    path: 'statements',
    component: lazyRouteComponent(retryImport(() => import('./lessons/LessonStatementPage/LessonStatementPage'))),
    loader: async ({ params: { lessonSlug } }) => {
      const lesson = await queryClient.ensureQueryData(lessonBySlugQueryOptions(lessonSlug));
      await queryClient.ensureQueryData(lessonStatementLanguagesQueryOptions(lesson.jid));
    },
  });

  const adminLessonStatementLanguagesRoute = createRoute({
    getParentRoute: () => adminLessonRoute,
    path: 'languages',
    component: lazyRouteComponent(
      retryImport(() => import('./lessons/LessonStatementLanguagesPage/LessonStatementLanguagesPage'))
    ),
    loader: async ({ params: { lessonSlug } }) => {
      const lesson = await queryClient.ensureQueryData(lessonBySlugQueryOptions(lessonSlug));
      await queryClient.ensureQueryData(lessonStatementLanguagesQueryOptions(lesson.jid));
    },
  });

  const adminLessonStatementMediaRoute = createRoute({
    getParentRoute: () => adminLessonRoute,
    path: 'media',
    component: lazyRouteComponent(
      retryImport(() => import('./lessons/LessonStatementMediaPage/LessonStatementMediaPage'))
    ),
    loader: async ({ params: { lessonSlug } }) => {
      const lesson = await queryClient.ensureQueryData(lessonBySlugQueryOptions(lessonSlug));
      await queryClient.ensureQueryData(lessonStatementMediaFilesQueryOptions(lesson.jid));
    },
  });

  const adminLessonVersionsRoute = createRoute({
    getParentRoute: () => adminLessonRoute,
    path: 'versions',
    component: lazyRouteComponent(retryImport(() => import('./lessons/LessonVersionsPage/LessonVersionsPage'))),
    loader: async ({ params: { lessonSlug } }) => {
      const lesson = await queryClient.ensureQueryData(lessonBySlugQueryOptions(lessonSlug));
      await queryClient.ensureQueryData(lessonVersionsQueryOptions(lesson.jid));
    },
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
      adminProblemEditorialLayoutRoute.addChildren([
        adminProblemEditorialIndexRoute,
        adminProblemEditorialRoute,
        adminProblemEditorialLanguagesRoute,
        adminProblemEditorialMediaRoute,
      ]),
      adminProblemItemsRoute,
      adminProblemItemRoute,
      adminProblemGradingRoute.addChildren([
        adminProblemGradingIndexRoute,
        adminProblemGradingEngineRoute,
        adminProblemGradingConfigRoute,
        adminProblemGradingTestDataRoute,
        adminProblemGradingHelpersRoute,
        adminProblemGradingLanguageRestrictionRoute,
      ]),
      adminProblemVersionsRoute,
    ]),
    adminLessonsRoute,
    adminLessonRoute.addChildren([
      adminLessonIndexRoute,
      adminLessonGeneralRoute,
      adminLessonStatementRoute,
      adminLessonStatementLanguagesRoute,
      adminLessonStatementMediaRoute,
      adminLessonVersionsRoute,
    ]),
    adminSettingsRoute,
  ]);
};
