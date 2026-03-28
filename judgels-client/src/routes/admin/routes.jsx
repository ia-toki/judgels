import { Navigate, createRoute, lazyRouteComponent } from '@tanstack/react-router';

import { isTLX } from '../../conf';
import { retryImport } from '../../lazy';
import { courseBySlugQueryOptions, courseChaptersQueryOptions } from '../../modules/queries/course';
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

  const adminUserViewRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'users/$username',
    component: lazyRouteComponent(retryImport(() => import('./users/UserViewPage/UserViewPage'))),
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

  const adminCourseViewRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'courses/$courseSlug',
    component: lazyRouteComponent(retryImport(() => import('./courses/CourseViewPage/CourseViewPage'))),
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

  const adminArchivesRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'archives',
    component: lazyRouteComponent(retryImport(() => import('./archives/ArchivesPage/ArchivesPage'))),
  });

  const adminProblemSetsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'problemsets',
    component: lazyRouteComponent(retryImport(() => import('./problemsets/ProblemSetsPage/ProblemSetsPage'))),
  });

  return adminRoute.addChildren([
    adminIndexRoute,
    adminUsersRoute,
    adminUserViewRoute,
    adminRolesRoute,
    adminRatingsRoute,
    adminCoursesRoute,
    adminCourseViewRoute,
    adminChaptersRoute,
    adminArchivesRoute,
    adminProblemSetsRoute,
  ]);
};
