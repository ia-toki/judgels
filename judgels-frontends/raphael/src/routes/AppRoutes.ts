import { JophielRole } from 'modules/api/jophiel/my';

import JophielRoutes from './jophiel/JophielRoutes';
import JophielAccountsRoutes from './jophiel/JophielAccountsRoutes';
import UrielContestsRoutes from './uriel/UrielContestsRoutes';
import JerahmeelRoutes from './jerahmeel/JerahmeelRoutes';
import JudgelsRankingRoutes from './ranking/JudgelsRankingRoutes';

function shouldShowRoute(id: string, role: JophielRole) {
  if (id === 'account' && role !== JophielRole.Superadmin && role !== JophielRole.Admin) {
    return false;
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
      component: UrielContestsRoutes,
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

export function getAppRoutes() {
  return appRoutes;
}

export function getVisibleAppRoutes(role: JophielRole) {
  return appRoutes.filter(route => shouldShowRoute(route.id, role));
}

export function getHomeRoute() {
  return homeRoute;
}
