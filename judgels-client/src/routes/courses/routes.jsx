import { Outlet, createRoute, lazyRouteComponent, redirect } from '@tanstack/react-router';

import { retryImport } from '../../lazy';
import { ProblemType } from '../../modules/api/sandalphon/problem';
import { chapterLessonStatementQueryOptions, chapterLessonsQueryOptions } from '../../modules/queries/chapterLesson';
import { chapterProblemWorksheetQueryOptions, chapterProblemsQueryOptions } from '../../modules/queries/chapterProblem';
import { chapterBundleLatestSubmissionsQueryOptions } from '../../modules/queries/chapterSubmissionBundle';
import { chapterProgrammingSubmissionsQueryOptions } from '../../modules/queries/chapterSubmissionProgramming';
import {
  courseBySlugQueryOptions,
  courseChapterQueryOptions,
  courseChaptersQueryOptions,
  coursesQueryOptions,
} from '../../modules/queries/course';
import { submissionWithSourceQueryOptions } from '../../modules/queries/submissionProgramming';
import { queryClient } from '../../modules/queryClient';
import { getUser } from '../../modules/session';
import { getWebPrefs } from '../../modules/webPrefs';
import { createDocumentTitle } from '../../utils/title';
import { isUserBlocked } from '../blockedUsernames';

export const createCoursesRoutes = appRoute => {
  const coursesRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'courses',
    component: Outlet,
    head: () => ({ meta: [{ title: createDocumentTitle('Courses') }] }),
    beforeLoad: () => {
      if (isUserBlocked(getUser())) {
        throw redirect({ to: '/' });
      }
    },
  });

  const coursesIndexRoute = createRoute({
    getParentRoute: () => coursesRoute,
    path: '/',
    component: lazyRouteComponent(retryImport(() => import('./CoursesIndexPage'))),
    loader: () => {
      queryClient.prefetchQuery(coursesQueryOptions());
    },
  });

  const courseRoute = createRoute({
    getParentRoute: () => coursesRoute,
    path: '$courseSlug',
    component: lazyRouteComponent(retryImport(() => import('./courses/single/SingleCourseLayout'))),
    loader: async ({ params: { courseSlug } }) => {
      const course = await queryClient.ensureQueryData(courseBySlugQueryOptions(courseSlug));
      queryClient.prefetchQuery(courseChaptersQueryOptions(course.jid));
    },
  });

  const courseIndexRoute = createRoute({
    getParentRoute: () => courseRoute,
    path: '/',
    component: lazyRouteComponent(retryImport(() => import('./courses/single/CourseOverview/CourseOverview'))),
    loader: async ({ params: { courseSlug } }) => {
      const course = await queryClient.ensureQueryData(courseBySlugQueryOptions(courseSlug));
      queryClient.prefetchQuery(courseChaptersQueryOptions(course.jid));
    },
  });

  const courseChapterRoute = createRoute({
    getParentRoute: () => courseRoute,
    path: 'chapters/$chapterAlias',
    component: lazyRouteComponent(
      retryImport(() => import('./courses/single/chapters/single/SingleCourseChapterLayout'))
    ),
    loader: async ({ params: { courseSlug, chapterAlias } }) => {
      const course = await queryClient.ensureQueryData(courseBySlugQueryOptions(courseSlug));
      await queryClient.ensureQueryData(courseChapterQueryOptions(course.jid, chapterAlias));
    },
  });

  const courseChapterResourcesRoute = createRoute({
    getParentRoute: () => courseChapterRoute,
    path: '/',
    component: lazyRouteComponent(
      retryImport(() => import('./courses/single/chapters/single/resources/ChapterResourcesPage/ChapterResourcesPage'))
    ),
    loader: async ({ params: { courseSlug, chapterAlias } }) => {
      const course = await queryClient.ensureQueryData(courseBySlugQueryOptions(courseSlug));
      const chapter = await queryClient.ensureQueryData(courseChapterQueryOptions(course.jid, chapterAlias));
      queryClient.prefetchQuery(chapterLessonsQueryOptions(chapter.jid));
      queryClient.prefetchQuery(chapterProblemsQueryOptions(chapter.jid));
    },
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
    loader: async ({ params: { courseSlug, chapterAlias, lessonAlias } }) => {
      const course = await queryClient.ensureQueryData(courseBySlugQueryOptions(courseSlug));
      const chapter = await queryClient.ensureQueryData(courseChapterQueryOptions(course.jid, chapterAlias));
      const language = getWebPrefs().statementLanguage;
      queryClient.prefetchQuery(chapterLessonStatementQueryOptions(chapter.jid, lessonAlias, { language }));
    },
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
    loader: async ({ params: { courseSlug, chapterAlias, problemAlias } }) => {
      const course = await queryClient.ensureQueryData(courseBySlugQueryOptions(courseSlug));
      const chapter = await queryClient.ensureQueryData(courseChapterQueryOptions(course.jid, chapterAlias));
      const language = getWebPrefs().statementLanguage;
      queryClient
        .fetchQuery(chapterProblemWorksheetQueryOptions(chapter.jid, problemAlias, { language }))
        .then(worksheet => {
          if (worksheet?.problem?.type === ProblemType.Bundle) {
            queryClient.prefetchQuery(chapterBundleLatestSubmissionsQueryOptions(chapter.jid, problemAlias));
          }
        });
    },
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
    loader: async ({ params: { courseSlug, chapterAlias, problemAlias } }) => {
      const course = await queryClient.ensureQueryData(courseBySlugQueryOptions(courseSlug));
      const chapter = await queryClient.ensureQueryData(courseChapterQueryOptions(course.jid, chapterAlias));
      const username = getUser()?.username;
      queryClient.prefetchQuery(chapterProgrammingSubmissionsQueryOptions(chapter.jid, { problemAlias, username }));
    },
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
    loader: async ({ params: { courseSlug, chapterAlias, problemAlias } }) => {
      const course = await queryClient.ensureQueryData(courseBySlugQueryOptions(courseSlug));
      const chapter = await queryClient.ensureQueryData(courseChapterQueryOptions(course.jid, chapterAlias));
      queryClient.prefetchQuery(chapterProgrammingSubmissionsQueryOptions(chapter.jid, { problemAlias }));
    },
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
    loader: ({ params: { submissionId } }) => {
      const language = getWebPrefs().statementLanguage;
      queryClient.prefetchQuery(submissionWithSourceQueryOptions(+submissionId, { language }));
    },
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
