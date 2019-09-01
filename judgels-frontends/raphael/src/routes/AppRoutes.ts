import { APP_CONFIG, Mode } from '../conf';
import { JophielRole } from '../modules/api/jophiel/role';

import JophielRoutes from './jophiel/JophielRoutes';
import JophielAccountsRoutes from './jophiel/JophielAccountsRoutes';
import UrielRoutes, { LoadableUrielRoutes } from './uriel/UrielRoutes';
import JerahmeelRoutes from './jerahmeel/JerahmeelRoutes';
import JudgelsRankingRoutes from './ranking/JudgelsRankingRoutes';

function shouldShowRoute(id: string, role: JophielRole) {
  if (id === 'account') {
    return role === JophielRole.Superadmin || role === JophielRole.Admin;
  }
  if (id === 'training' || id === 'ranking') {
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
      component: JophielAccountsRoutes,
    },
  },
  {
    id: 'contests',
    title: 'Contests',
    route: {
      path: '/contests',
      component: UrielRoutes,
    },
  },
  {
    id: 'training',
    title: 'Training',
    route: {
      path: '/training',
      component: JerahmeelRoutes,
    },
  },
  {
    id: 'ranking',
    title: 'Ranking',
    route: {
      path: '/ranking',
      component: JudgelsRankingRoutes,
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
    LoadableUrielRoutes.preload();
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
