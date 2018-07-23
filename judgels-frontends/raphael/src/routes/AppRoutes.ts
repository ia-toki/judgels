import JophielRoutes from './jophiel/JophielRoutes';
import JophielAccountsRoutes from './jophiel/JophielAccountsRoutes';
import UrielContestsRoutes from './uriel/UrielContestsRoutes';
import JerahmeelRoutes from './jerahmeel/JerahmeelRoutes';
import { JophielRole } from '../modules/api/jophiel/my';

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
];

const homeRoute = {
  id: 'home',
  title: 'Home',
  route: {
    component: JophielRoutes,
  },
};

export function getAppRoutes(role: JophielRole) {
  return appRoutes.filter(route => shouldShowRoute(route.id, role));
}

export function getHomeRoute() {
  return homeRoute;
}
