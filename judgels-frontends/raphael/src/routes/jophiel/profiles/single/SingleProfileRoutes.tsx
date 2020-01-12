import * as React from 'react';
import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import { FullPageLayout } from '../../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { AppState } from '../../../../modules/store';

import ProfileSummaryPage from './summary/ProfileSummaryPage/ProfileSummaryPage';
import ContestHistoryPage from './contestHistory/ContestHistoryPage/ContestHistoryPage';
import SubmissionHistoryPage from './submissionHistory/SubmissionHistoryPage/SubmissionHistoryPage';
import { selectUserJid, selectUsername } from '../../modules/profileSelectors';

interface SingleProfileRoutesProps {
  userJid?: string;
  username?: string;
}

const SingleProfileRoutes = (props: SingleProfileRoutesProps) => {
  const { userJid, username } = props;
  if (!userJid) {
    return <LoadingState large />;
  }

  const sidebarItems: ContentWithSidebarItem[] = [
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
    {
      id: 'submission-history',
      titleIcon: 'layers',
      title: 'Submission history',
      routeComponent: Route,
      component: SubmissionHistoryPage,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
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
};

function createSingleProfileRoutes() {
  const mapStateToProps = (state: AppState) => ({
    userJid: selectUserJid(state),
    username: selectUsername(state),
  });
  return withRouter<any, any>(connect(mapStateToProps)(SingleProfileRoutes));
}

export default createSingleProfileRoutes();
