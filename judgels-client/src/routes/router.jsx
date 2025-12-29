import { createBrowserRouter } from 'react-router';

import { LoadingState } from '../components/LoadingState/LoadingState';
import App from './App';
import Root from './Root';
import { jophielRoutes } from './jophiel/JophielRoutes';
import { systemRoutes } from './system/SystemRoutes';

export const lazyRoutes = {
  contests: () => import('./contests/ContestsRoutes'),
  courses: () => import('./courses/CoursesRoutes'),
  problems: () => import('./problems/ProblemsRoutes'),
  training: () => import('./training/TrainingRoutes'),
  submissions: () => import('./submissions/SubmissionsRoutes'),
  ranking: () => import('./ranking/RankingRoutes'),
};

async function patchRoutesOnNavigation({ path, patch }) {
  for (const [prefix, importFn] of Object.entries(lazyRoutes)) {
    if (path.startsWith(`/${prefix}`)) {
      const module = await importFn();
      patch('app', module.routes);
      delete lazyRoutes[prefix]; // Don't re-patch once loaded
      return;
    }
  }
}

export const router = createBrowserRouter(
  [
    {
      path: '/',
      element: <Root />,
      children: [
        {
          id: 'app',
          element: <App />,
          children: [
            ...systemRoutes,
            jophielRoutes,
            {
              path: '*',
              element: <LoadingState large />,
            },
          ],
        },
      ],
    },
  ],
  {
    patchRoutesOnNavigation,
  }
);
