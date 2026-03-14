import { Console } from '@blueprintjs/icons';

import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullWidthPageLayout } from '../../components/FullWidthPageLayout/FullWidthPageLayout';
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
    <FullWidthPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps}>
        <ContestsPage />
      </ContentWithSidebar>
    </FullWidthPageLayout>
  );
}
