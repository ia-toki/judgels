import { Layers, Properties, TimelineEvents } from '@blueprintjs/icons';
import { useSelector } from 'react-redux';
import { Route } from 'react-router';
import { useParams } from 'react-router-dom';

import ContentWithSidebar from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { isTLX } from '../../../../conf';
import { selectUserJid, selectUsername } from '../../modules/profileSelectors';
import ContestHistoryPage from './contestHistory/ContestHistoryPage/ContestHistoryPage';
import SubmissionHistoryPage from './submissionHistory/SubmissionHistoryPage/SubmissionHistoryPage';
import ProfileSummaryPage from './summary/ProfileSummaryPage/ProfileSummaryPage';

export default function SingleProfileRoutes() {
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
      id: '@',
      titleIcon: <Properties />,
      title: 'Summary',
      routeComponent: Route,
      component: ProfileSummaryPage,
    },
    {
      id: 'contest-history',
      titleIcon: <TimelineEvents />,
      title: 'Contest history',
      routeComponent: Route,
      component: ContestHistoryPage,
    },
    ...(isTLX()
      ? [
          {
            id: 'submission-history',
            titleIcon: <Layers />,
            title: 'Submission history',
            routeComponent: Route,
            component: SubmissionHistoryPage,
          },
        ]
      : []),
  ];

  const contentWithSidebarProps = {
    title: 'Profile Menu',
    items: sidebarItems,
    contentHeader: <h2>Profile of {username}</h2>,
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
}
