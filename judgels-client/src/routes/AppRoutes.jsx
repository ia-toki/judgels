import { Console, Home, Key, Layers, Manual, PredictiveAnalysis, TimelineLineChart } from '@blueprintjs/icons';

import { isTLX } from '../conf';
import { JerahmeelRole } from '../modules/api/jerahmeel/role';
import { JophielRole } from '../modules/api/jophiel/role';
import { SandalphonRole } from '../modules/api/sandalphon/role';
import { UrielRole } from '../modules/api/uriel/role';

const appRoutes = [
  {
    id: 'admin',
    icon: <Key />,
    title: 'Admin',
    route: {
      path: '/admin',
    },
    visible: role =>
      role.jophiel === JophielRole.Superadmin ||
      role.jophiel === JophielRole.Admin ||
      role.uriel === UrielRole.Admin ||
      role.jerahmeel === JerahmeelRole.Admin,
  },
  {
    id: 'contests',
    icon: <Console />,
    title: 'Contests',
    route: {
      path: '/contests',
    },
    visible: () => true,
  },
  {
    id: 'courses',
    icon: <PredictiveAnalysis />,
    title: 'Courses',
    route: {
      path: '/courses',
    },
    visible: () => isTLX(),
  },
  {
    id: 'problems',
    icon: <Manual />,
    title: 'Problems',
    route: {
      path: '/problems',
    },
    visible: () => isTLX(),
  },
  {
    id: 'submissions',
    icon: <Layers />,
    title: 'Submissions',
    route: {
      path: '/submissions',
    },
    visible: () => isTLX(),
  },
  {
    id: 'ranking',
    icon: <TimelineLineChart />,
    title: 'Ranking',
    route: {
      path: '/ranking',
    },
    visible: () => isTLX(),
  },
];

const homeRoute = {
  id: 'home',
  icon: <Home />,
  title: 'Home',
  route: {},
};

export function getVisibleAppRoutes(role) {
  return appRoutes.filter(route => route.visible(role));
}

export function getHomeRoute() {
  return homeRoute;
}
