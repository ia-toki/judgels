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

import { isInPrivateContestsMode, hasJerahmeel } from '../conf';
import { JophielRole } from '../modules/api/jophiel/role';
import { JerahmeelRole } from '../modules/api/jerahmeel/role';

import JophielRoutes from './jophiel/JophielRoutes';
import LazySystemRoutes from './system/LazySystemRoutes';
import LazyContestsRoutes, { ContestsRoutesPromise } from './contests/LazyContestsRoutes';
import LazyCoursesRoutes from './courses/LazyCoursesRoutes';
import LazyTrainingRoutes from './training/LazyTrainingRoutes';
import LazyProblemsRoutes from './problems/LazyProblemsRoutes';
import LazySubmissionsRoutes from './submissions/LazySubmissionsRoutes';
import LazyRankingRoutes from './ranking/LazyRankingRoutes';

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
    visible: role => hasJerahmeel() && role.jerahmeel === JerahmeelRole.Admin,
  },
  {
    id: 'courses',
    icon: <PredictiveAnalysis />,
    title: 'Courses',
    route: {
      path: '/courses',
      component: LazyCoursesRoutes,
    },
    visible: () => !isInPrivateContestsMode(),
  },
  {
    id: 'problems',
    icon: <Manual />,
    title: 'Problems',
    route: {
      path: '/problems',
      component: LazyProblemsRoutes,
    },
    visible: () => !isInPrivateContestsMode(),
  },
  {
    id: 'submissions',
    icon: <Layers />,
    title: 'Submissions',
    route: {
      path: '/submissions',
      component: LazySubmissionsRoutes,
    },
    visible: () => !isInPrivateContestsMode(),
  },
  {
    id: 'ranking',
    icon: <TimelineLineChart />,
    title: 'Ranking',
    route: {
      path: '/ranking',
      component: LazyRankingRoutes,
    },
    visible: () => !isInPrivateContestsMode(),
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
  if (isInPrivateContestsMode()) {
    ContestsRoutesPromise();
  }
}

export function getVisibleAppRoutes(role) {
  return appRoutes.filter(route => route.visible(role));
}

export function getHomeRoute() {
  return homeRoute;
}
