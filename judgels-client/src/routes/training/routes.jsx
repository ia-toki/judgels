import { Navigate, createRoute, lazyRouteComponent } from '@tanstack/react-router';

import { retryImport } from '../../lazy';
import { createDocumentTitle } from '../../utils/title';

export const createTrainingRoutes = appRoute => {
  const trainingRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'training',
    component: lazyRouteComponent(retryImport(() => import('./TrainingLayout'))),
    head: () => ({ meta: [{ title: createDocumentTitle('Training') }] }),
  });

  const trainingIndexRoute = createRoute({
    getParentRoute: () => trainingRoute,
    path: '/',
    component: () => <Navigate to="/training/courses" />,
  });

  const trainingCoursesRoute = createRoute({
    getParentRoute: () => trainingRoute,
    path: 'courses',
    component: lazyRouteComponent(retryImport(() => import('./courses/CoursesPage/CoursesPage'))),
  });

  const trainingChaptersRoute = createRoute({
    getParentRoute: () => trainingRoute,
    path: 'chapters',
    component: lazyRouteComponent(retryImport(() => import('./chapters/ChaptersPage/ChaptersPage'))),
  });

  const trainingArchivesRoute = createRoute({
    getParentRoute: () => trainingRoute,
    path: 'archives',
    component: lazyRouteComponent(retryImport(() => import('./archives/ArchivesPage/ArchivesPage'))),
  });

  const trainingProblemSetsRoute = createRoute({
    getParentRoute: () => trainingRoute,
    path: 'problemsets',
    component: lazyRouteComponent(retryImport(() => import('./problemsets/ProblemSetsPage/ProblemSetsPage'))),
  });

  return trainingRoute.addChildren([
    trainingIndexRoute,
    trainingCoursesRoute,
    trainingChaptersRoute,
    trainingArchivesRoute,
    trainingProblemSetsRoute,
  ]);
};
