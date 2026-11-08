import { Console, Home, Key, Layers, Manual, PredictiveAnalysis, TimelineLineChart } from '@blueprintjs/icons';

import { isRatingEnabled, isTrainingEnabled } from '../conf';
import { isContestAdmin, isProblemWriter, isSystemAdmin, isTrainingAdmin } from '../modules/api/userRole';

const appRoutes = [
  {
    id: 'admin',
    icon: <Key />,
    title: 'Admin',
    route: {
      path: '/admin',
    },
    visible: roles =>
      isSystemAdmin(roles) ||
      isProblemWriter(roles) ||
      isContestAdmin(roles) ||
      (isTrainingEnabled() && isTrainingAdmin(roles)),
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
    visible: () => isTrainingEnabled(),
  },
  {
    id: 'problems',
    icon: <Manual />,
    title: 'Problems',
    route: {
      path: '/problems',
    },
    visible: () => isTrainingEnabled(),
  },
  {
    id: 'submissions',
    icon: <Layers />,
    title: 'Submissions',
    route: {
      path: '/submissions',
    },
    visible: () => isTrainingEnabled(),
  },
  {
    id: 'ranking',
    icon: <TimelineLineChart />,
    title: 'Ranking',
    route: {
      path: '/ranking',
    },
    visible: () => isRatingEnabled(),
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
