import { Console, Home, Key, Layers, Manual, PredictiveAnalysis, TimelineLineChart } from '@blueprintjs/icons';

import { isTLX } from '../conf';
import { isContestAdmin, isSystemAdmin, isTrainingAdmin } from '../modules/api/userRole';

const appRoutes = [
  {
    id: 'admin',
    icon: <Key />,
    title: 'Admin',
    route: {
      path: '/admin',
    },
    visible: roles => isSystemAdmin(roles) || isContestAdmin(roles) || (isTLX() && isTrainingAdmin(roles)),
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

export function getVisibleAppRoutes(roles) {
  return appRoutes.filter(route => route.visible(roles));
}

export function getHomeRoute() {
  return homeRoute;
}
