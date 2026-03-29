import { Navigate, createRoute, lazyRouteComponent } from '@tanstack/react-router';

import { isTLX } from '../../conf';
import { retryImport } from '../../lazy';
import { archiveBySlugQueryOptions } from '../../modules/queries/archive';
import { archivesQueryOptions } from '../../modules/queries/archive';
import { chapterByJidQueryOptions } from '../../modules/queries/chapter';
import { chapterLessonsQueryOptions } from '../../modules/queries/chapterLesson';
import { chapterProblemsQueryOptions } from '../../modules/queries/chapterProblem';
import { courseBySlugQueryOptions, courseChaptersQueryOptions } from '../../modules/queries/course';
import { problemSetBySlugQueryOptions, problemSetProblemsQueryOptions } from '../../modules/queries/problemSet';
import { userByUsernameQueryOptions } from '../../modules/queries/user';
import { userInfoQueryOptions } from '../../modules/queries/userInfo';
import { queryClient } from '../../modules/queryClient';
import { createDocumentTitle } from '../../utils/title';

export const createAdminRoutes = appRoute => {
  if (!isTLX()) {
    return null;
  }

  const adminRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'admin',
    component: lazyRouteComponent(retryImport(() => import('./AdminLayout'))),
    head: () => ({ meta: [{ title: createDocumentTitle('Admin') }] }),
  });

  const adminIndexRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: '/',
    component: () => <Navigate to="/admin/users" />,
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
