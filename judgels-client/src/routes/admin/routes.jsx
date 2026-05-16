import { createRoute, lazyRouteComponent } from '@tanstack/react-router';

import { retryImport } from '../../lazy';
import { archiveBySlugQueryOptions } from '../../modules/queries/adminArchive';
import { archivesQueryOptions } from '../../modules/queries/adminArchive';
import { chapterByJidQueryOptions } from '../../modules/queries/adminChapter';
import { chapterLessonsQueryOptions } from '../../modules/queries/adminChapterLesson';
import { chapterProblemsQueryOptions } from '../../modules/queries/adminChapterProblem';
import { courseBySlugQueryOptions } from '../../modules/queries/adminCourse';
import { courseChaptersQueryOptions } from '../../modules/queries/adminCourseChapter';
import { problemSetBySlugQueryOptions } from '../../modules/queries/adminProblemSet';
import { problemSetProblemsQueryOptions } from '../../modules/queries/adminProblemSetProblem';
import { userByUsernameQueryOptions } from '../../modules/queries/adminUser';
import { adminUserInfoQueryOptions } from '../../modules/queries/adminUserInfo';
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
      await queryClient.ensureQueryData(adminUserInfoQueryOptions(user.jid));
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

  return adminRoute.addChildren([
    adminIndexRoute,
    adminUsersRoute,
    adminUserRoute,
    adminRolesRoute,
    adminRatingsRoute,
    adminContestsRoute,
    adminCoursesRoute,
    adminCourseRoute,
    adminChaptersRoute,
    adminChapterRoute,
    adminArchivesRoute,
    adminArchiveRoute,
    adminProblemSetsRoute,
    adminProblemSetRoute,
  ]);
};
