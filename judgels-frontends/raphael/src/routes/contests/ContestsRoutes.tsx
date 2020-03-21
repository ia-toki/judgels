import * as React from 'react';
import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../components/ContentWithSidebar/ContentWithSidebar';
import { AppState } from '../../modules/store';
import { UserRole } from '../../modules/api/jophiel/role';

import ContestsPage from './contests/ContestsPage/ContestsPage';
import { selectRole } from '../jophiel/modules/userWebSelectors';

interface ContestRoutesProps {
  role: UserRole;
}

const ContestsRoutes = (props: ContestRoutesProps) => {
  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: '@',
      titleIcon: 'timeline-events',
      title: 'Contests',
      routeComponent: Route,
      component: ContestsPage,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Menu',
    items: sidebarItems,
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
};

const mapStateToProps = (state: AppState) => ({
  role: selectRole(state),
});
export default withRouter<any, any>(connect(mapStateToProps)(ContestsRoutes));
