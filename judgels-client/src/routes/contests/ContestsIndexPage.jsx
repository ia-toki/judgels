import { Console } from '@blueprintjs/icons';

import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ContestsPage from './contests/ContestsPage/ContestsPage';

export default function ContestsIndexPage() {
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
    basePath: '/contests',
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps}>
        <ContestsPage />
      </ContentWithSidebar>
    </FullPageLayout>
  );
}
