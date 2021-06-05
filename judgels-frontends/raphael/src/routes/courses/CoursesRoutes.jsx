import { PredictiveAnalysis } from '@blueprintjs/icons';
import { Route } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';

import CoursesPage from './courses/CoursesPage/CoursesPage';

function CoursesRoutes() {
  const sidebarItems = [
    {
      id: '@',
      titleIcon: <PredictiveAnalysis />,
      title: 'Courses',
      routeComponent: Route,
      component: CoursesPage,
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

export default CoursesRoutes;
