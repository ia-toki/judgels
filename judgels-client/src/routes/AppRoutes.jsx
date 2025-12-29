import {
  Cog,
  Console,
  Home,
  Layers,
  Manual,
  PredictiveAnalysis,
  Projects,
  TimelineLineChart,
} from '@blueprintjs/icons';

import { isTLX } from '../conf';
import { JerahmeelRole } from '../modules/api/jerahmeel/role';
import { JophielRole } from '../modules/api/jophiel/role';
import { lazyRoutes } from './router';

const appRoutes = [
  {
    id: 'system',
    icon: <Cog />,
    title: 'System',
    route: {
      path: '/system',
    },
    visible: role => isTLX() && (role.jophiel === JophielRole.Superadmin || role.jophiel === JophielRole.Admin),
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
    id: 'training',
    icon: <Projects />,
    title: 'Training',
    route: {
      path: '/training',
    },
    visible: role => isTLX() && role.jerahmeel === JerahmeelRole.Admin,
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

export function preloadRoutes() {
  if (!isTLX()) {
    lazyRoutes.contests();
  }
}

export function getVisibleAppRoutes(role) {
  return appRoutes.filter(route => route.visible(role));
}

export function getHomeRoute() {
  return homeRoute;
}
