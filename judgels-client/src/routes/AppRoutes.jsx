import { Console, Home, Key, Layers, Manual, PredictiveAnalysis, TimelineLineChart } from '@blueprintjs/icons';

import { isTLX } from '../conf';
import { JerahmeelRole } from '../modules/api/jerahmeel/role';
import { JophielRole } from '../modules/api/jophiel/role';
import { SandalphonRole } from '../modules/api/sandalphon/role';
import { UrielRole } from '../modules/api/uriel/role';
import { isUserBlocked } from './blockedUsernames';

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
      (isTLX() && role.jerahmeel === JerahmeelRole.Admin),
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
    visible: (role, user) => isTLX() && !isUserBlocked(user),
  },
  {
    id: 'problems',
    icon: <Manual />,
    title: 'Problems',
    route: {
      path: '/problems',
    },
    visible: (role, user) => isTLX() && !isUserBlocked(user),
  },
  {
    id: 'submissions',
    icon: <Layers />,
    title: 'Submissions',
    route: {
      path: '/submissions',
    },
    visible: (role, user) => isTLX() && !isUserBlocked(user),
  },
  {
    id: 'ranking',
    icon: <TimelineLineChart />,
    title: 'Ranking',
    route: {
      path: '/ranking',
    },
    visible: (role, user) => isTLX() && !isUserBlocked(user),
  },
];

const homeRoute = {
  id: 'home',
  icon: <Home />,
  title: 'Home',
  route: {},
};

export function getVisibleAppRoutes(role, user) {
  return appRoutes.filter(route => route.visible(role, user));
}

export function getHomeRoute() {
  return homeRoute;
}
