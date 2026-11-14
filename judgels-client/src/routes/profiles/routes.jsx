import { createRoute, lazyRouteComponent } from '@tanstack/react-router';

import EnabledRoute from '../../components/EnabledRoute/EnabledRoute';
import { retryImport } from '../../lazy';
import { contestPublicHistoryQueryOptions } from '../../modules/queries/contestHistory';
import { basicProfileByUsernameQueryOptions } from '../../modules/queries/profile';
import { profileSubmissionsQueryOptions } from '../../modules/queries/trainingSubmission';
import { trainingUserStatsQueryOptions } from '../../modules/queries/trainingUserStats';
import { userAvatarUrlQueryOptions } from '../../modules/queries/userAvatar';
import { queryClient } from '../../modules/queryClient';
import { isTrainingEnabled } from '../../modules/webConfig';
import { createDocumentTitle } from '../../utils/title';

const SubmissionHistoryPage = lazyRouteComponent(
  retryImport(() => import('./single/submissionHistory/SubmissionHistoryPage/SubmissionHistoryPage'))
);

export const createProfilesRoutes = appRoute => {
  const profilesRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'profiles/$username',
    component: lazyRouteComponent(retryImport(() => import('./ProfilesLayout'))),
    head: () => ({ meta: [{ title: createDocumentTitle('Profiles') }] }),
    loader: async ({ params: { username } }) => {
      await queryClient.ensureQueryData(basicProfileByUsernameQueryOptions(username));
    },
  });

  const profileIndexRoute = createRoute({
    getParentRoute: () => profilesRoute,
    path: '/',
    component: lazyRouteComponent(retryImport(() => import('./single/summary/ProfileSummaryPage/ProfileSummaryPage'))),
    loader: async ({ params: { username } }) => {
      const { userJid } = await queryClient.ensureQueryData(basicProfileByUsernameQueryOptions(username));
      queryClient.prefetchQuery(userAvatarUrlQueryOptions(userJid));
      if (isTrainingEnabled()) {
        queryClient.prefetchQuery(trainingUserStatsQueryOptions(username));
      }
    },
  });

  const profileContestHistoryRoute = createRoute({
    getParentRoute: () => profilesRoute,
    path: 'contest-history',
    component: lazyRouteComponent(
      retryImport(() => import('./single/contestHistory/ContestHistoryPage/ContestHistoryPage'))
    ),
    loader: ({ params: { username } }) => {
      queryClient.prefetchQuery(contestPublicHistoryQueryOptions(username));
    },
  });

  const profileSubmissionHistoryRoute = createRoute({
    getParentRoute: () => profilesRoute,
    path: 'submission-history',
    component: () => (
      <EnabledRoute enabled={isTrainingEnabled()}>
        <SubmissionHistoryPage />
      </EnabledRoute>
    ),
    loader: ({ params: { username }, search = {} }) => {
      if (isTrainingEnabled()) {
        queryClient.prefetchQuery(
          profileSubmissionsQueryOptions(username, { beforeId: search.before, afterId: search.after })
        );
      }
    },
  });

  return profilesRoute.addChildren([profileIndexRoute, profileContestHistoryRoute, profileSubmissionHistoryRoute]);
};
