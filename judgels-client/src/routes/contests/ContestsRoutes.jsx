import { Console } from '@blueprintjs/icons';
import { Route, withRouter } from 'react-router';

import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ContestsPage from './contests/ContestsPage/ContestsPage';

function ContestsRoutes() {
  const sidebarItems = [
    {
      id: '@',
      titleIcon: <Console />,
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
