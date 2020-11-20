import * as React from 'react';
import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import { APP_CONFIG, Mode } from '../../../../conf';
import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import ContentWithSidebar from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import ProfileSummaryPage from './summary/ProfileSummaryPage/ProfileSummaryPage';
import ContestHistoryPage from './contestHistory/ContestHistoryPage/ContestHistoryPage';
import SubmissionHistoryPage from './submissionHistory/SubmissionHistoryPage/SubmissionHistoryPage';
import { selectUserJid, selectUsername } from '../../modules/profileSelectors';

function SingleProfileRoutes({ userJid, username }) {
  if (!userJid) {
    return <LoadingState large />;
  }

  const sidebarItems = [
    {
      id: '@',
      titleIcon: 'properties',
      title: 'Summary',
      routeComponent: Route,
      component: ProfileSummaryPage,
    },
    {
      id: 'contest-history',
      titleIcon: 'timeline-events',
      title: 'Contest history',
      routeComponent: Route,
      component: ContestHistoryPage,
    },
    ...(APP_CONFIG.mode === Mode.PRIVATE_CONTESTS
      ? []
      : [
          {
            id: 'submission-history',
            titleIcon: 'layers',
            title: 'Submission history',
            routeComponent: Route,
            component: SubmissionHistoryPage,
          },
        ]),
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
