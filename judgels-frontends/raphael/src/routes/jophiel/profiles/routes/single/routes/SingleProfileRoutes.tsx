import * as React from 'react';
import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import { FullPageLayout } from '../../../../../../components/layouts/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../../../../../components/ContentWithSidebar/ContentWithSidebar';
import { AppState } from '../../../../../../modules/store';
import ProfileSummaryPage from './summary/ProfileSummaryPage/ProfileSummaryPage';
import { selectUserJid } from '../../../../modules/profileSelectors';

interface SingleProfileRoutesProps {
  userJid?: string;
}

const SingleProfileRoutes = (props: SingleProfileRoutesProps) => {
  const { userJid } = props;
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
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Profile Menu',
    items: sidebarItems,
    smallContent: true,
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
  });
  return withRouter<any>(connect(mapStateToProps)(SingleProfileRoutes));
}

export default createSingleProfileRoutes();
