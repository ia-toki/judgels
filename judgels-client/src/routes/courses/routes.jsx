import { Outlet, createRoute, lazyRouteComponent } from '@tanstack/react-router';

import { retryImport } from '../../lazy';
import { createDocumentTitle } from '../../utils/title';

export const createCoursesRoutes = appRoute => {
  const coursesRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'courses',
    component: Outlet,
    head: () => ({ meta: [{ title: createDocumentTitle('Courses') }] }),
  });

  const coursesIndexRoute = createRoute({
    getParentRoute: () => coursesRoute,
    path: '/',
    component: lazyRouteComponent(retryImport(() => import('./CoursesIndexPage'))),
  });

  const courseRoute = createRoute({
    getParentRoute: () => coursesRoute,
    path: '$courseSlug',
    component: lazyRouteComponent(retryImport(() => import('./courses/single/SingleCourseLayout'))),
  });

  const courseIndexRoute = createRoute({
    getParentRoute: () => courseRoute,
    path: '/',
    component: lazyRouteComponent(retryImport(() => import('./courses/single/CourseOverview/CourseOverview'))),
  });

  const courseChapterRoute = createRoute({
    getParentRoute: () => courseRoute,
    path: 'chapters/$chapterAlias',
    component: lazyRouteComponent(
      retryImport(() => import('./courses/single/chapters/single/SingleCourseChapterLayout'))
    ),
  });

  const courseChapterResourcesRoute = createRoute({
    getParentRoute: () => courseChapterRoute,
    path: '/',
    component: lazyRouteComponent(
      retryImport(() => import('./courses/single/chapters/single/resources/ChapterResourcesPage/ChapterResourcesPage'))
    ),
  });

  const courseChapterLessonsRoute = createRoute({
    getParentRoute: () => courseChapterRoute,
    path: 'lessons',
    component: Outlet,
  });

  const courseChapterLessonRoute = createRoute({
    getParentRoute: () => courseChapterLessonsRoute,
    path: '$lessonAlias',
    component: lazyRouteComponent(
      retryImport(() => import('./courses/single/chapters/single/lessons/single/ChapterLessonPage/ChapterLessonPage'))
    ),
  });

  const courseChapterProblemsRoute = createRoute({
    getParentRoute: () => courseChapterRoute,
    path: 'problems',
    component: Outlet,
  });

  const courseChapterProblemRoute = createRoute({
    getParentRoute: () => courseChapterProblemsRoute,
    path: '$problemAlias',
    component: lazyRouteComponent(
      retryImport(() => import('./courses/single/chapters/single/problems/single/ChapterProblemLayout'))
    ),
  });

  const courseChapterProblemWorkspaceRoute = createRoute({
    getParentRoute: () => courseChapterProblemRoute,
    path: '/',
    component: lazyRouteComponent(
      retryImport(
        () =>
          import(
            './courses/single/chapters/single/problems/single/Programming/ChapterProblemWorkspacePage/ChapterProblemWorkspacePage'
          )
      )
    ),
  });

  const courseChapterProblemSubmissionsRoute = createRoute({
    getParentRoute: () => courseChapterProblemRoute,
    path: 'submissions',
    component: lazyRouteComponent(
      retryImport(
        () =>
          import(
            './courses/single/chapters/single/problems/single/Programming/submissions/ChapterProblemSubmissionsPage/ChapterProblemSubmissionsPage'
          )
      )
    ),
  });

  const courseChapterProblemSubmissionsAllRoute = createRoute({
    getParentRoute: () => courseChapterProblemRoute,
    path: 'submissions/all',
    component: lazyRouteComponent(
      retryImport(
        () =>
          import(
            './courses/single/chapters/single/problems/single/Programming/submissions/ChapterProblemSubmissionsPage/ChapterProblemSubmissionsPage'
          )
      )
    ),
  });

  const courseChapterProblemSubmissionRoute = createRoute({
    getParentRoute: () => courseChapterProblemRoute,
    path: 'submissions/$submissionId',
    component: lazyRouteComponent(
      retryImport(
        () =>
          import(
            './courses/single/chapters/single/problems/single/Programming/submissions/single/ChapterProblemSubmissionPage/ChapterProblemSubmissionPage'
          )
      )
    ),
  });

  return coursesRoute.addChildren([
    coursesIndexRoute,
    courseRoute.addChildren([
      courseIndexRoute,
      courseChapterRoute.addChildren([
        courseChapterResourcesRoute,
        courseChapterLessonsRoute.addChildren([courseChapterLessonRoute]),
        courseChapterProblemsRoute.addChildren([
          courseChapterProblemRoute.addChildren([
            courseChapterProblemWorkspaceRoute,
            courseChapterProblemSubmissionsRoute,
            courseChapterProblemSubmissionsAllRoute,
            courseChapterProblemSubmissionRoute,
          ]),
        ]),
      ]),
    ]),
  ]);
};
