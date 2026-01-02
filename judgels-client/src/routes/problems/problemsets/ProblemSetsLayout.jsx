import { Manual, PanelStats } from '@blueprintjs/icons';
import { Outlet } from '@tanstack/react-router';

import ContentWithSidebar from '../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import ProblemTagFilter from '../problems/ProblemTagFilter/ProblemTagFilter';
import ProblemSetArchiveFilter from './ProblemSetArchiveFilter/ProblemSetArchiveFilter';

export default function ProblemSetsLayout() {
  const sidebarItems = [
    {
      path: '',
      titleIcon: <Manual />,
      title: 'Browse problems',
      widgetComponent: ProblemTagFilter,
    },
    {
      path: 'problemsets',
      titleIcon: <PanelStats />,
      title: 'Browse problemsets',
      widgetComponent: ProblemSetArchiveFilter,
    },
  ];

  return (
    <FullPageLayout>
      <ContentWithSidebar title="Menu" items={sidebarItems} basePath="/problems">
        <Outlet />
      </ContentWithSidebar>
    </FullPageLayout>
  );
}
