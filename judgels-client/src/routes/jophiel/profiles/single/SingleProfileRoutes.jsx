import { Layers, Properties, TimelineEvents } from '@blueprintjs/icons';
import { useSelector } from 'react-redux';
import { Outlet, useParams } from 'react-router';

import ContentWithSidebar from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { isTLX } from '../../../../conf';
import { selectUserJid, selectUsername } from '../../modules/profileSelectors';
import ContestHistoryPage from './contestHistory/ContestHistoryPage/ContestHistoryPage';
import SubmissionHistoryPage from './submissionHistory/SubmissionHistoryPage/SubmissionHistoryPage';
import ProfileSummaryPage from './summary/ProfileSummaryPage/ProfileSummaryPage';

export const singleProfileRoutes = [
  {
    index: true,
    element: <ProfileSummaryPage />,
  },
  {
    path: 'contest-history',
    element: <ContestHistoryPage />,
  },
  ...(isTLX()
    ? [
        {
          path: 'submission-history',
          element: <SubmissionHistoryPage />,
        },
      ]
    : []),
];

export function SingleProfileLayout() {
  const { username: paramUsername } = useParams();
  const userJid = useSelector(selectUserJid);
  const username = useSelector(selectUsername);

  // Optimization:
  // We wait until we get the username from the backend only if the current username is different from the persisted one.
  if (!userJid || username !== paramUsername) {
    return <LoadingState large />;
  }

  const sidebarItems = [
    {
      path: '',
      titleIcon: <Properties />,
      title: 'Summary',
    },
    {
      path: 'contest-history',
      titleIcon: <TimelineEvents />,
      title: 'Contest history',
    },
    ...(isTLX()
      ? [
          {
            path: 'submission-history',
            titleIcon: <Layers />,
            title: 'Submission history',
          },
        ]
      : []),
  ];

  const contentWithSidebarProps = {
    title: 'Profile Menu',
    items: sidebarItems,
    contentHeader: <h2>Profile of {username}</h2>,
    basePath: `/profiles/${username}`,
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Outlet />
      </ContentWithSidebar>
    </FullPageLayout>
  );
}
