import { createRoute, lazyRouteComponent, redirect } from '@tanstack/react-router';

import { retryImport } from '../../lazy';
import { archiveBySlugQueryOptions } from '../../modules/queries/archive';
import { archivesQueryOptions } from '../../modules/queries/archive';
import { chapterByJidQueryOptions } from '../../modules/queries/chapter';
import { chapterLessonsQueryOptions } from '../../modules/queries/chapterLesson';
import { chapterProblemsQueryOptions } from '../../modules/queries/chapterProblem';
import { contestsPendingRatingQueryOptions } from '../../modules/queries/contestRating';
import { courseBySlugQueryOptions } from '../../modules/queries/course';
import { courseChaptersQueryOptions } from '../../modules/queries/courseChapter';
import { curriculumByJidQueryOptions } from '../../modules/queries/curriculum';
import { lessonBySlugQueryOptions, lessonQueryOptions } from '../../modules/queries/lesson';
import {
  lessonStatementLanguagesQueryOptions,
  lessonStatementMediaFilesQueryOptions,
} from '../../modules/queries/lessonStatement';
import {
  lessonHasVersionLocalChangesQueryOptions,
  lessonVersionsQueryOptions,
} from '../../modules/queries/lessonVersion';
import {
  problemBySlugQueryOptions,
  problemPermissionsQueryOptions,
  problemQueryOptions,
} from '../../modules/queries/problem';
import {
  problemEditorialLanguagesQueryOptions,
  problemEditorialMediaFilesQueryOptions,
  problemHasEditorialQueryOptions,
} from '../../modules/queries/problemEditorial';
import {
  problemGradingConfigQueryOptions,
  problemGradingHelperFilesQueryOptions,
  problemGradingLanguageRestrictionQueryOptions,
  problemGradingTestDataFilesQueryOptions,
} from '../../modules/queries/problemGrading';
import { problemItemsQueryOptions } from '../../modules/queries/problemItem';
import { problemPartnersQueryOptions } from '../../modules/queries/problemPartner';
import { problemSetBySlugQueryOptions } from '../../modules/queries/problemSet';
import { problemSetProblemsQueryOptions } from '../../modules/queries/problemSetProblem';
import { problemSettersQueryOptions } from '../../modules/queries/problemSetter';
import {
  problemStatementLanguagesQueryOptions,
  problemStatementMediaFilesQueryOptions,
} from '../../modules/queries/problemStatement';
import { problemTopicTagsQueryOptions } from '../../modules/queries/problemTag';
import {
  problemHasVersionLocalChangesQueryOptions,
  problemVersionsQueryOptions,
} from '../../modules/queries/problemVersion';
import { problemWritersQueryOptions } from '../../modules/queries/problemWriter';
import { basicProfileQueryOptions } from '../../modules/queries/profile';
import { userByUsernameQueryOptions } from '../../modules/queries/user';
import { userInfoQueryOptions } from '../../modules/queries/userInfo';
import { userRolesQueryOptions } from '../../modules/queries/userRole';
import { queryClient } from '../../modules/queryClient';
import { isRatingEnabled, isTrainingEnabled } from '../../modules/webConfig';
import { createDocumentTitle } from '../../utils/title';

function requireRatingEnabled() {
  if (!isRatingEnabled()) {
    throw redirect({ to: '/admin', replace: true });
  }
}

function requireTrainingEnabled() {
  if (!isTrainingEnabled()) {
    throw redirect({ to: '/admin', replace: true });
  }
}

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

  const adminUserUpsertRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'users/-/upsert',
    component: lazyRouteComponent(retryImport(() => import('./users/upsert/UserUpsertPage/UserUpsertPage'))),
  });

  const adminUserRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'users/$username',
    component: lazyRouteComponent(retryImport(() => import('./users/single/UserPage/UserPage'))),
    loader: async ({ params: { username } }) => {
      const user = await queryClient.ensureQueryData(userByUsernameQueryOptions(username));
      await queryClient.ensureQueryData(userInfoQueryOptions(user.jid));
    },
  });

  const adminRolesRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'roles',
    component: lazyRouteComponent(retryImport(() => import('./roles/RolesIndexPage'))),
  });

  const adminRoleAdminsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'roles/admins',
    component: lazyRouteComponent(retryImport(() => import('./roles/admins/RolesPage/RolesPage'))),
  });

  const adminRoleUpsertRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'roles/admins/-/upsert',
    component: lazyRouteComponent(retryImport(() => import('./roles/admins/upsert/RoleUpsertPage/RoleUpsertPage'))),
    loader: async () => {
      await queryClient.ensureQueryData(userRolesQueryOptions());
    },
  });

  const adminProblemWritersRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'roles/problem-writers',
    component: lazyRouteComponent(
      retryImport(() => import('./roles/problemWriters/ProblemWritersPage/ProblemWritersPage'))
    ),
    loader: async () => {
      await queryClient.ensureQueryData(problemWritersQueryOptions());
    },
  });

  const adminProblemWritersEditRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'roles/problem-writers/edit',
    component: lazyRouteComponent(
      retryImport(() => import('./roles/problemWriters/edit/ProblemWritersEditPage/ProblemWritersEditPage'))
    ),
    loader: async () => {
      await queryClient.ensureQueryData(problemWritersQueryOptions());
    },
  });

  const adminRoleEditRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'roles/admins/$username',
    component: lazyRouteComponent(retryImport(() => import('./roles/admins/single/RoleEditPage/RoleEditPage'))),
    loader: async () => {
      await queryClient.ensureQueryData(userRolesQueryOptions());
    },
  });

  const adminRatingsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'ratings',
    beforeLoad: requireRatingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./ratings/RatingsPage/RatingsPage'))),
  });

  const adminContestRatingChangesRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'ratings/$contestSlug',
    beforeLoad: requireRatingEnabled,
    component: lazyRouteComponent(
      retryImport(() => import('./ratings/single/ContestRatingChangesPage/ContestRatingChangesPage'))
    ),
    loader: async () => {
      await queryClient.ensureQueryData(contestsPendingRatingQueryOptions());
    },
  });

  const adminContestsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'contests',
    component: lazyRouteComponent(retryImport(() => import('./contests/ContestsPage/ContestsPage'))),
  });

  const adminContestCreateRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'contests/-/new',
    component: lazyRouteComponent(retryImport(() => import('./contests/new/ContestCreatePage/ContestCreatePage'))),
  });

  const adminCurriculumsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'curriculums',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./curriculums/CurriculumsPage/CurriculumsPage'))),
  });

  const adminCurriculumRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'curriculums/$curriculumJid',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./curriculums/single/CurriculumPage/CurriculumPage'))),
    loader: async ({ params: { curriculumJid } }) => {
      await queryClient.ensureQueryData(curriculumByJidQueryOptions(curriculumJid));
    },
  });

  const adminCoursesRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'courses',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./courses/CoursesPage/CoursesPage'))),
  });

  const adminCourseCreateRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'courses/-/new',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./courses/new/CourseCreatePage/CourseCreatePage'))),
  });

  const adminCourseRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'courses/$courseSlug',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./courses/single/CoursePage/CoursePage'))),
    loader: async ({ params: { courseSlug } }) => {
      const course = await queryClient.ensureQueryData(courseBySlugQueryOptions(courseSlug));
      await queryClient.ensureQueryData(courseChaptersQueryOptions(course.jid));
    },
  });

  const adminChaptersRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'chapters',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./chapters/ChaptersPage/ChaptersPage'))),
  });

  const adminChapterCreateRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'chapters/-/new',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./chapters/new/ChapterCreatePage/ChapterCreatePage'))),
  });

  const adminChapterRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'chapters/$chapterJid',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./chapters/single/ChapterPage/ChapterPage'))),
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
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./archives/ArchivesPage/ArchivesPage'))),
  });

  const adminArchiveCreateRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'archives/-/new',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./archives/new/ArchiveCreatePage/ArchiveCreatePage'))),
  });

  const adminArchiveRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'archives/$archiveSlug',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./archives/single/ArchivePage/ArchivePage'))),
    loader: async ({ params: { archiveSlug } }) => {
      await queryClient.ensureQueryData(archiveBySlugQueryOptions(archiveSlug));
    },
  });

  const adminProblemSetsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'problemsets',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./problemsets/ProblemSetsPage/ProblemSetsPage'))),
  });

  const adminProblemSetCreateRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'problemsets/-/new',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(
      retryImport(() => import('./problemsets/new/ProblemSetCreatePage/ProblemSetCreatePage'))
    ),
  });

  const adminProblemSetRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'problemsets/$problemSetSlug',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./problemsets/single/ProblemSetPage/ProblemSetPage'))),
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

  const adminProblemCreateRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'problems/-/new',
    component: lazyRouteComponent(retryImport(() => import('./problems/new/ProblemCreatePage/ProblemCreatePage'))),
  });

  const adminProblemRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'problems/$problemSlug',
    component: lazyRouteComponent(retryImport(() => import('./problems/single/ProblemLayout'))),
    loader: async ({ params: { problemSlug } }) => {
      const { jid: problemJid } = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await Promise.all([
        queryClient.ensureQueryData(problemQueryOptions(problemJid)),
        queryClient.ensureQueryData(problemPermissionsQueryOptions(problemJid)),
        queryClient.ensureQueryData(problemHasVersionLocalChangesQueryOptions(problemJid)),
      ]);
    },
  });

  const adminProblemIndexRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: '/',
    beforeLoad: ({ params }) => {
      throw redirect({ to: '/admin/problems/$problemSlug/statements', params, replace: true });
    },
  });

  const adminProblemGeneralRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'general',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/single/general/ProblemGeneralPage/ProblemGeneralPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const { jid: problemJid } = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      const problem = await queryClient.ensureQueryData(problemQueryOptions(problemJid));
      await Promise.all([
        queryClient.ensureQueryData(basicProfileQueryOptions(problem.authorJid)),
        queryClient.ensureQueryData(problemSettersQueryOptions(problemJid)),
        queryClient.ensureQueryData(problemTopicTagsQueryOptions(problemJid)),
      ]);
    },
  });

  const adminProblemStatementRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'statements',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/single/statements/ProblemStatementPage/ProblemStatementPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemStatementLanguagesQueryOptions(problem.jid));
    },
  });

  const adminProblemStatementLanguagesRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'languages',
    component: lazyRouteComponent(
      retryImport(
        () => import('./problems/single/languages/ProblemStatementLanguagesPage/ProblemStatementLanguagesPage')
      )
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
      retryImport(() => import('./problems/single/media/ProblemStatementMediaPage/ProblemStatementMediaPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemStatementMediaFilesQueryOptions(problem.jid));
    },
  });

  // A problem without an editorial has nothing to load: its layout offers to create one instead.
  const ensureProblemEditorialQueryData = async (problemJid, editorialQueryOptions) => {
    const hasEditorial = await queryClient.ensureQueryData(problemHasEditorialQueryOptions(problemJid));
    if (hasEditorial) {
      await queryClient.ensureQueryData(editorialQueryOptions(problemJid));
    }
  };

  const adminProblemEditorialLayoutRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'editorial',
    component: lazyRouteComponent(retryImport(() => import('./problems/single/editorial/ProblemEditorialLayout'))),
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
    component: lazyRouteComponent(
      retryImport(() => import('./problems/single/editorial/content/ProblemEditorialPage/ProblemEditorialPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await ensureProblemEditorialQueryData(problem.jid, problemEditorialLanguagesQueryOptions);
    },
  });

  const adminProblemEditorialLanguagesRoute = createRoute({
    getParentRoute: () => adminProblemEditorialLayoutRoute,
    path: 'languages',
    component: lazyRouteComponent(
      retryImport(
        () =>
          import('./problems/single/editorial/languages/ProblemEditorialLanguagesPage/ProblemEditorialLanguagesPage')
      )
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
      retryImport(() => import('./problems/single/editorial/media/ProblemEditorialMediaPage/ProblemEditorialMediaPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await ensureProblemEditorialQueryData(problem.jid, problemEditorialMediaFilesQueryOptions);
    },
  });

  const adminProblemItemsRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'items',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/single/items/ProblemItemsPage/ProblemItemsPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemItemsQueryOptions(problem.jid));
    },
  });

  const adminProblemItemRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'items/$itemJid',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/single/items/single/ProblemItemPage/ProblemItemPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemStatementLanguagesQueryOptions(problem.jid));
    },
  });

  const adminProblemGradingRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'grading',
    component: lazyRouteComponent(retryImport(() => import('./problems/single/grading/ProblemGradingLayout'))),
  });

  const adminProblemGradingIndexRoute = createRoute({
    getParentRoute: () => adminProblemGradingRoute,
    path: '/',
    beforeLoad: ({ params }) => {
      throw redirect({ to: '/admin/problems/$problemSlug/grading/config', params, replace: true });
    },
  });

  const adminProblemGradingEngineRoute = createRoute({
    getParentRoute: () => adminProblemGradingRoute,
    path: 'engine',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/single/grading/engine/ProblemGradingEnginePage/ProblemGradingEnginePage'))
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
      retryImport(() => import('./problems/single/grading/config/ProblemGradingConfigPage/ProblemGradingConfigPage'))
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
      retryImport(
        () => import('./problems/single/grading/testData/ProblemGradingTestDataPage/ProblemGradingTestDataPage')
      )
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
      retryImport(() => import('./problems/single/grading/helpers/ProblemGradingHelpersPage/ProblemGradingHelpersPage'))
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
        () =>
          import(
            './problems/single/grading/languages/ProblemGradingLanguageRestrictionPage/ProblemGradingLanguageRestrictionPage'
          )
      )
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemGradingLanguageRestrictionQueryOptions(problem.jid));
    },
  });

  const adminProblemSubmissionsRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'submissions',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/single/submissions/ProblemSubmissionsPage/ProblemSubmissionsPage'))
    ),
  });

  const adminProblemSubmissionRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'submissions/$submissionId',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/single/submissions/single/ProblemSubmissionPage/ProblemSubmissionPage'))
    ),
  });

  const adminProblemVersionsRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'versions',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/single/versions/ProblemVersionsPage/ProblemVersionsPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemVersionsQueryOptions(problem.jid));
    },
  });

  const adminProblemPartnersRoute = createRoute({
    getParentRoute: () => adminProblemRoute,
    path: 'partners',
    component: lazyRouteComponent(
      retryImport(() => import('./problems/single/partners/ProblemPartnersPage/ProblemPartnersPage'))
    ),
    loader: async ({ params: { problemSlug } }) => {
      const problem = await queryClient.ensureQueryData(problemBySlugQueryOptions(problemSlug));
      await queryClient.ensureQueryData(problemPartnersQueryOptions(problem.jid));
    },
  });

  const adminLessonsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'lessons',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./lessons/LessonsPage/LessonsPage'))),
  });

  const adminLessonCreateRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'lessons/-/new',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./lessons/new/LessonCreatePage/LessonCreatePage'))),
  });

  const adminLessonRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'lessons/$lessonSlug',
    beforeLoad: requireTrainingEnabled,
    component: lazyRouteComponent(retryImport(() => import('./lessons/single/LessonLayout'))),
    loader: async ({ params: { lessonSlug } }) => {
      const { jid: lessonJid } = await queryClient.ensureQueryData(lessonBySlugQueryOptions(lessonSlug));
      await Promise.all([
        queryClient.ensureQueryData(lessonQueryOptions(lessonJid)),
        queryClient.ensureQueryData(lessonHasVersionLocalChangesQueryOptions(lessonJid)),
      ]);
    },
  });

  const adminLessonIndexRoute = createRoute({
    getParentRoute: () => adminLessonRoute,
    path: '/',
    beforeLoad: ({ params }) => {
      throw redirect({ to: '/admin/lessons/$lessonSlug/statements', params, replace: true });
    },
  });

  const adminLessonGeneralRoute = createRoute({
    getParentRoute: () => adminLessonRoute,
    path: 'general',
    component: lazyRouteComponent(
      retryImport(() => import('./lessons/single/general/LessonGeneralPage/LessonGeneralPage'))
    ),
    loader: async ({ params: { lessonSlug } }) => {
      const { jid: lessonJid } = await queryClient.ensureQueryData(lessonBySlugQueryOptions(lessonSlug));
      const lesson = await queryClient.ensureQueryData(lessonQueryOptions(lessonJid));
      await queryClient.ensureQueryData(basicProfileQueryOptions(lesson.authorJid));
    },
  });

  const adminLessonStatementRoute = createRoute({
    getParentRoute: () => adminLessonRoute,
    path: 'statements',
    component: lazyRouteComponent(
      retryImport(() => import('./lessons/single/statements/LessonStatementPage/LessonStatementPage'))
    ),
    loader: async ({ params: { lessonSlug } }) => {
      const lesson = await queryClient.ensureQueryData(lessonBySlugQueryOptions(lessonSlug));
      await queryClient.ensureQueryData(lessonStatementLanguagesQueryOptions(lesson.jid));
    },
  });

  const adminLessonStatementLanguagesRoute = createRoute({
    getParentRoute: () => adminLessonRoute,
    path: 'languages',
    component: lazyRouteComponent(
      retryImport(() => import('./lessons/single/languages/LessonStatementLanguagesPage/LessonStatementLanguagesPage'))
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
      retryImport(() => import('./lessons/single/media/LessonStatementMediaPage/LessonStatementMediaPage'))
    ),
    loader: async ({ params: { lessonSlug } }) => {
      const lesson = await queryClient.ensureQueryData(lessonBySlugQueryOptions(lessonSlug));
      await queryClient.ensureQueryData(lessonStatementMediaFilesQueryOptions(lesson.jid));
    },
  });

  const adminLessonVersionsRoute = createRoute({
    getParentRoute: () => adminLessonRoute,
    path: 'versions',
    component: lazyRouteComponent(
      retryImport(() => import('./lessons/single/versions/LessonVersionsPage/LessonVersionsPage'))
    ),
    loader: async ({ params: { lessonSlug } }) => {
      const lesson = await queryClient.ensureQueryData(lessonBySlugQueryOptions(lessonSlug));
      await queryClient.ensureQueryData(lessonVersionsQueryOptions(lesson.jid));
    },
  });

  const adminSettingsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'settings',
    component: lazyRouteComponent(retryImport(() => import('./settings/SettingsIndexPage'))),
  });

  const adminGeneralSettingsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'settings/general',
    component: lazyRouteComponent(
      retryImport(() => import('./settings/general/GeneralSettingsPage/GeneralSettingsPage'))
    ),
  });

  const adminTrainingSettingsRoute = createRoute({
    getParentRoute: () => adminRoute,
    path: 'settings/training',
    beforeLoad: () => {
      if (!isTrainingEnabled()) {
        throw redirect({ to: '/admin/settings/general', replace: true });
      }
    },
    component: lazyRouteComponent(
      retryImport(() => import('./settings/training/TrainingSettingsPage/TrainingSettingsPage'))
    ),
  });

  return adminRoute.addChildren([
    adminIndexRoute,
    adminUsersRoute,
    adminUserUpsertRoute,
    adminUserRoute,
    adminRolesRoute,
    adminRoleAdminsRoute,
    adminRoleUpsertRoute,
    adminProblemWritersRoute,
    adminProblemWritersEditRoute,
    adminRoleEditRoute,
    adminRatingsRoute,
    adminContestRatingChangesRoute,
    adminContestsRoute,
    adminContestCreateRoute,
    adminCurriculumsRoute,
    adminCurriculumRoute,
    adminCoursesRoute,
    adminCourseCreateRoute,
    adminCourseRoute,
    adminChaptersRoute,
    adminChapterCreateRoute,
    adminChapterRoute,
    adminArchivesRoute,
    adminArchiveCreateRoute,
    adminArchiveRoute,
    adminProblemSetsRoute,
    adminProblemSetCreateRoute,
    adminProblemSetRoute,
    adminProblemsRoute,
    adminProblemCreateRoute,
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
      adminProblemSubmissionsRoute,
      adminProblemSubmissionRoute,
      adminProblemVersionsRoute,
      adminProblemPartnersRoute,
    ]),
    adminLessonsRoute,
    adminLessonCreateRoute,
    adminLessonRoute.addChildren([
      adminLessonIndexRoute,
      adminLessonGeneralRoute,
      adminLessonStatementRoute,
      adminLessonStatementLanguagesRoute,
      adminLessonStatementMediaRoute,
      adminLessonVersionsRoute,
    ]),
    adminSettingsRoute,
    adminGeneralSettingsRoute,
    adminTrainingSettingsRoute,
  ]);
};
