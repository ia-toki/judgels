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
import LazyContestsRoutes, { ContestsRoutesPromise } from './contests/LazyContestsRoutes';
import LazyCoursesRoutes from './courses/LazyCoursesRoutes';
import JophielRoutes from './jophiel/JophielRoutes';
import LazyProblemsRoutes from './problems/LazyProblemsRoutes';
import LazyRankingRoutes from './ranking/LazyRankingRoutes';
import LazySubmissionsRoutes from './submissions/LazySubmissionsRoutes';
import LazySystemRoutes from './system/LazySystemRoutes';
import LazyTrainingRoutes from './training/LazyTrainingRoutes';

const appRoutes = [
  {
    id: 'system',
    icon: <Cog />,
    title: 'System',
    route: {
      path: '/system',
      component: LazySystemRoutes,
    },
    visible: role => role.jophiel === JophielRole.Superadmin || role.jophiel === JophielRole.Admin,
  },
  {
    id: 'contests',
    icon: <Console />,
    title: 'Contests',
    route: {
      path: '/contests',
      component: LazyContestsRoutes,
    },
    visible: () => true,
  },
  {
    id: 'training',
    icon: <Projects />,
    title: 'Training',
    route: {
      path: '/training',
      component: LazyTrainingRoutes,
    },
    visible: role => isTLX() && role.jerahmeel === JerahmeelRole.Admin,
  },
  {
    id: 'courses',
    icon: <PredictiveAnalysis />,
    title: 'Courses',
    route: {
      path: '/courses',
      component: LazyCoursesRoutes,
    },
    visible: () => isTLX(),
  },
  {
    id: 'problems',
    icon: <Manual />,
    title: 'Problems',
    route: {
      path: '/problems',
      component: LazyProblemsRoutes,
    },
    visible: () => isTLX(),
  },
  {
    id: 'submissions',
    icon: <Layers />,
    title: 'Submissions',
    route: {
      path: '/submissions',
      component: LazySubmissionsRoutes,
    },
    visible: () => isTLX(),
  },
  {
    id: 'ranking',
    icon: <TimelineLineChart />,
    title: 'Ranking',
    route: {
      path: '/ranking',
      component: LazyRankingRoutes,
    },
    visible: () => isTLX(),
  },
];

const homeRoute = {
  id: 'home',
  icon: <Home />,
  title: 'Home',
  route: {
    component: JophielRoutes,
  },
};

export function preloadRoutes() {
  if (!isTLX()) {
    ContestsRoutesPromise();
  }
}

export function getVisibleAppRoutes(role) {
  return appRoutes.filter(route => route.visible(role));
}

export function getHomeRoute() {
  return homeRoute;
}
