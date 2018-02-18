import * as React from 'react';
import { Route } from 'react-router';

import { FullPageLayout } from '../../../../components/layouts/FullPageLayout/FullPageLayout';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import ContestRoutes from './contests/routes/ContestRoutes';
import { withBreadcrumb } from '../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';

const CompetitionRoutes = () => {
  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: 'contests',
      title: 'Contests',
      routeComponent: Route,
      component: ContestRoutes,
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
