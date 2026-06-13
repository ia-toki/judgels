import { createRoute, lazyRouteComponent } from '@tanstack/react-router';

import { isTLX } from '../../conf';
import { retryImport } from '../../lazy';
import {
  basicProfileQueryOptions,
  profileContestHistoryQueryOptions,
  profileSubmissionsQueryOptions,
  userJidByUsernameQueryOptions,
} from '../../modules/queries/profile';
import { userStatsQueryOptions } from '../../modules/queries/stats';
import { avatarUrlQueryOptions } from '../../modules/queries/userAvatar';
import { queryClient } from '../../modules/queryClient';
import { createDocumentTitle } from '../../utils/title';
import HomePage from './HomePage/HomePage';

export const createHomeRoutes = appRoute => {
  const homeRoute = createRoute({
    getParentRoute: () => appRoute,
    path: '/',
    component: HomePage,
  });

  const profilesRoute = createRoute({
    getParentRoute: () => appRoute,
    path: 'profiles/$username',
    component: lazyRouteComponent(retryImport(() => import('./profiles/ProfilesLayout'))),
    head: () => ({ meta: [{ title: createDocumentTitle('Profiles') }] }),
    loader: async ({ params: { username } }) => {
      await queryClient.ensureQueryData(userJidByUsernameQueryOptions(username));
    },
  });

  const profileIndexRoute = createRoute({
    getParentRoute: () => profilesRoute,
    path: '/',
    component: lazyRouteComponent(
      retryImport(() => import('./profiles/single/summary/ProfileSummaryPage/ProfileSummaryPage'))
    ),
    loader: async ({ params: { username } }) => {
      const userJid = await queryClient.ensureQueryData(userJidByUsernameQueryOptions(username));
      queryClient.prefetchQuery(avatarUrlQueryOptions(userJid));
      queryClient.prefetchQuery(basicProfileQueryOptions(userJid));
      if (isTLX()) {
        queryClient.prefetchQuery(userStatsQueryOptions(username));
      }
    },
  });

  const profileContestHistoryRoute = createRoute({
    getParentRoute: () => profilesRoute,
    path: 'contest-history',
    component: lazyRouteComponent(
      retryImport(() => import('./profiles/single/contestHistory/ContestHistoryPage/ContestHistoryPage'))
    ),
    loader: ({ params: { username } }) => {
      queryClient.prefetchQuery(profileContestHistoryQueryOptions(username));
    },
  });

  const profileSubmissionHistoryRoute = isTLX()
    ? createRoute({
        getParentRoute: () => profilesRoute,
        path: 'submission-history',
        component: lazyRouteComponent(
          retryImport(() => import('./profiles/single/submissionHistory/SubmissionHistoryPage/SubmissionHistoryPage'))
        ),
        loader: ({ params: { username }, search = {} }) => {
          queryClient.prefetchQuery(
            profileSubmissionsQueryOptions(username, { beforeId: search.before, afterId: search.after })
          );
        },
      })
    : null;

  return [
    homeRoute,
    profilesRoute.addChildren(
      [profileIndexRoute, profileContestHistoryRoute, profileSubmissionHistoryRoute].filter(Boolean)
    ),
  ];
};
