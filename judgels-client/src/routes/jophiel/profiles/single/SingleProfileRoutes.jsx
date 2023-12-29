import { Layers, Properties, TimelineEvents } from '@blueprintjs/icons';
import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import ContentWithSidebar from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { isTLX } from '../../../../conf';
import { selectUserJid, selectUsername } from '../../modules/profileSelectors';
import ContestHistoryPage from './contestHistory/ContestHistoryPage/ContestHistoryPage';
import SubmissionHistoryPage from './submissionHistory/SubmissionHistoryPage/SubmissionHistoryPage';
import ProfileSummaryPage from './summary/ProfileSummaryPage/ProfileSummaryPage';

function SingleProfileRoutes({ match, userJid, username }) {
  // Optimization:
  // We wait until we get the username from the backend only if the current username is different from the persisted one.
  if (!userJid || username !== match.params.username) {
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

function createSingleProfileRoutes() {
  const mapStateToProps = state => ({
    userJid: selectUserJid(state),
    username: selectUsername(state),
  });
  return withRouter(connect(mapStateToProps)(SingleProfileRoutes));
}

export default createSingleProfileRoutes();
