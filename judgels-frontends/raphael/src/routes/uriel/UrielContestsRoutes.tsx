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
import MainAdminsRoutes from './admins/MainAdminsRoutes';
import { selectRole } from '../jophiel/modules/userWebSelectors';

interface ContestRoutesProps {
  role: JophielRole;
}

const UrielContestsRoutes = (props: ContestRoutesProps) => {
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
      component: MainAdminsRoutes,
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

function createUrielContestsRoutes() {
  const mapStateToProps = (state: AppState) => ({
    role: selectRole(state),
  });
  return withRouter<any>(connect(mapStateToProps)(UrielContestsRoutes));
}

export default createUrielContestsRoutes();
