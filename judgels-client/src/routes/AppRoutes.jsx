import { Console, Home, Key, Layers, Manual, PredictiveAnalysis, TimelineLineChart } from '@blueprintjs/icons';

import { isTLX } from '../conf';
import { ContestAdminRole } from '../modules/api/contestAdminRole';
import { TrainingAdminRole } from '../modules/api/trainingAdminRole';
import { UserAdminRole } from '../modules/api/userAdminRole';

const appRoutes = [
  {
    id: 'admin',
    icon: <Key />,
    title: 'Admin',
    route: {
      path: '/admin',
    },
    visible: role =>
      role.jophiel === UserAdminRole.Superadmin ||
      role.jophiel === UserAdminRole.Admin ||
      role.uriel === ContestAdminRole.Admin ||
      (isTLX() && role.jerahmeel === TrainingAdminRole.Admin),
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
