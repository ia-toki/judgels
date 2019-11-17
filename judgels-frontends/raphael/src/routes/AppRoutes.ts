import { APP_CONFIG, Mode } from '../conf';
import { JophielRole } from '../modules/api/jophiel/role';

import JophielRoutes from './jophiel/JophielRoutes';
import LazyAccountsRoutes from './accounts/LazyAccountsRoutes';
import LazyContestsRoutes, { ContestsRoutesPromise } from './contests/LazyContestsRoutes';
import LazyCoursesRoutes from './courses/LazyCoursesRoutes';
import LazyProblemsRoutes from './problems/LazyProblemsRoutes';
import LazyRankingRoutes from './ranking/LazyRankingRoutes';

function shouldShowRoute(id: string, role: JophielRole) {
  if (id === 'account') {
    return role === JophielRole.Superadmin || role === JophielRole.Admin;
  }
  if (id === 'courses' || id === 'problems') {
    return APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS && role === JophielRole.Superadmin;
  }
  if (id === 'ranking') {
    return APP_CONFIG.mode !== Mode.PRIVATE_CONTESTS;
  }
  return true;
}

const appRoutes = [
  {
    id: 'account',
    title: 'Accounts',
    route: {
      path: '/accounts',
      component: LazyAccountsRoutes,
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

export function getVisibleAppRoutes(role: JophielRole) {
  return appRoutes.filter(route => shouldShowRoute(route.id, role));
}

export function getHomeRoute() {
  return homeRoute;
}
