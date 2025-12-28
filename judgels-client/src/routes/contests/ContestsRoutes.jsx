import { Console } from '@blueprintjs/icons';
import { Route, Routes } from 'react-router';

import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ContestsPage from './contests/ContestsPage/ContestsPage';

export default function ContestsRoutes() {
  const sidebarItems = [
    {
      path: '',
      titleIcon: <Console />,
      title: 'Contests',
    },
  ];

  const contentWithSidebarProps = {
    title: 'Menu',
    items: sidebarItems,
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Routes>
          <Route index element={<ContestsPage />} />
        </Routes>
      </ContentWithSidebar>
    </FullPageLayout>
  );
}
