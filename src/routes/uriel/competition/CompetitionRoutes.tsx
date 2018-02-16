import * as React from 'react';
import { Route } from 'react-router';

import { FullPageLayout } from '../../../components/layouts/FullPageLayout/FullPageLayout';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../../components/ContentWithSidebar/ContentWithSidebar';
import ContestListPage from './routes/contests/ContestListPage/ContestListPage';
import { withBreadcrumb } from '../../../components/BreadcrumbWrapper/BreadcrumbWrapper';

const CompetitionRoutes = () => {
  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: 'contests',
      title: 'Contests',
      routeComponent: Route,
      component: ContestListPage,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Competition',
    items: sidebarItems,
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
};

export default withBreadcrumb('Competition')(CompetitionRoutes);
