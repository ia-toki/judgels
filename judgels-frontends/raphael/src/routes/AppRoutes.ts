import { isInPrivateContestsMode, hasJerahmeel } from '../conf';
import { UserRole, JophielRole } from '../modules/api/jophiel/role';
import { JerahmeelRole } from '../modules/api/jerahmeel/role';

import JophielRoutes from './jophiel/JophielRoutes';
import LazySystemRoutes from './system/LazySystemRoutes';
import LazyContestsRoutes, { ContestsRoutesPromise } from './contests/LazyContestsRoutes';
import LazyCoursesRoutes from './courses/LazyCoursesRoutes';
import LazyTrainingRoutes from './training/LazyTrainingRoutes';
import LazyProblemsRoutes from './problems/LazyProblemsRoutes';
import LazySubmissionsRoutes from './submissions/LazySubmissionsRoutes';
import LazyRankingRoutes from './ranking/LazyRankingRoutes';

const appRoutes = [
  {
    id: 'system',
    title: 'System',
    route: {
      path: '/system',
      component: LazySystemRoutes,
    },
    visible: (role: UserRole) => role.jophiel === JophielRole.Superadmin || role.jophiel === JophielRole.Admin,
  },
  {
    id: 'contests',
    title: 'Contests',
    route: {
      path: '/contests',
      component: LazyContestsRoutes,
    },
    visible: () => true,
  },
  {
    id: 'training',
    title: 'Training',
    route: {
      path: '/training',
      component: LazyTrainingRoutes,
    },
    visible: (role: UserRole) => hasJerahmeel() && role.jerahmeel === JerahmeelRole.Admin,
  },
  {
    id: 'courses',
    title: 'Courses',
    route: {
      path: '/courses',
      component: LazyCoursesRoutes,
    },
    visible: () => !isInPrivateContestsMode(),
  },
  {
    id: 'problems',
    title: 'Problems',
    route: {
      path: '/problems',
      component: LazyProblemsRoutes,
    },
    visible: () => !isInPrivateContestsMode(),
  },
  {
    id: 'submissions',
    title: 'Submissions',
    route: {
      path: '/submissions',
      component: LazySubmissionsRoutes,
    },
    visible: () => !isInPrivateContestsMode(),
  },
  {
    id: 'ranking',
    title: 'Ranking',
    route: {
      path: '/ranking',
      component: LazyRankingRoutes,
    },
    visible: () => !isInPrivateContestsMode(),
  },
];

const homeRoute = {
  id: 'home',
  title: 'Home',
  route: {
    component: JophielRoutes,
  },
};

export function preloadRoutes() {
  if (isInPrivateContestsMode()) {
    ContestsRoutesPromise();
  }
}

export function getVisibleAppRoutes(role: UserRole) {
  return appRoutes.filter(route => route.visible(role));
}

export function getHomeRoute() {
  return homeRoute;
}
