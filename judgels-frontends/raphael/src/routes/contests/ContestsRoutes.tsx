import * as React from 'react';
import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../components/ContentWithSidebar/ContentWithSidebar';
import { AppState } from '../../modules/store';
import { JophielRole } from '../../modules/api/jophiel/role';

import ContestsPage from './contests/ContestsPage/ContestsPage';
import LazyAdminsRoutes from './admins/LazyAdminsRoutes';
import { selectRole } from '../jophiel/modules/userWebSelectors';

interface ContestRoutesProps {
  role: JophielRole;
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
    {
      id: '_admins',
      titleIcon: 'id-number',
      title: 'Admins',
      routeComponent: Route,
      component: LazyAdminsRoutes,
      disabled: props.role !== JophielRole.Superadmin,
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

function createContestsRoutes() {
  const mapStateToProps = (state: AppState) => ({
    role: selectRole(state),
  });
  return withRouter<any, any>(connect(mapStateToProps)(ContestsRoutes));
}

export default createContestsRoutes();
