import { Route, withRouter } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import ContestsPage from './contests/ContestsPage/ContestsPage';

function ContestsRoutes() {
  const sidebarItems = [
    {
      id: '@',
      titleIcon: 'timeline-events',
      title: 'Contests',
      routeComponent: Route,
      component: ContestsPage,
    },
  ];

  const contentWithSidebarProps = {
    title: 'Menu',
    items: sidebarItems,
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
}

export default withRouter(ContestsRoutes);
