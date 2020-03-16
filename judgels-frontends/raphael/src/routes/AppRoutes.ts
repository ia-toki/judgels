import { APP_CONFIG, Mode } from '../conf';
import { UserRole, JophielRole } from '../modules/api/jophiel/role';

import JophielRoutes from './jophiel/JophielRoutes';
import LazySystemRoutes from './system/LazySystemRoutes';
import LazyContestsRoutes, { ContestsRoutesPromise } from './contests/LazyContestsRoutes';
import LazyCoursesRoutes from './courses/LazyCoursesRoutes';
import LazyProblemsRoutes from './problems/LazyProblemsRoutes';
import LazySubmissionsRoutes from './submissions/LazySubmissionsRoutes';
import LazyRankingRoutes from './ranking/LazyRankingRoutes';

function shouldShowRoute(id: string, role: UserRole) {
  if (id === 'system') {
    return role.jophiel === JophielRole.Superadmin || role.jophiel === JophielRole.Admin;
  }
  if (id === 'courses' || id === 'problems' || id === 'submissions') {
    return APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS;
  }
  if (id === 'ranking') {
    return APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS;
  }
  return true;
}

const appRoutes = [
  {
    id: 'system',
    title: 'System',
    route: {
      path: '/system',
      component: LazySystemRoutes,
    },
  },
  {
    id: 'contests',
    title: 'Contests',
    route: {
      path: '/contests',
      component: LazyContestsRoutes,
    },
  },
  {
    id: 'courses',
    title: 'Courses',
    route: {
      path: '/courses',
      component: LazyCoursesRoutes,
    },
  },
  {
    id: 'problems',
    title: 'Problems',
    route: {
      path: '/problems',
      component: LazyProblemsRoutes,
    },
  },
  {
    id: 'submissions',
    title: 'Submissions',
    route: {
      path: '/submissions',
      component: LazySubmissionsRoutes,
    },
  },
  {
    id: 'ranking',
    title: 'Ranking',
    route: {
      path: '/ranking',
      component: LazyRankingRoutes,
    },
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
  if (APP_CONFIG.mode === Mode.PRIVATE_CONTESTS) {
    ContestsRoutesPromise();
  }
}

export function getAppRoutes() {
  return appRoutes;
}

export function getVisibleAppRoutes(role: UserRole) {
  return appRoutes.filter(route => shouldShowRoute(route.id, role));
}

export function getHomeRoute() {
  return homeRoute;
}
