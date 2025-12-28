import { Manual, PanelStats } from '@blueprintjs/icons';

import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ProblemTagFilter from './problems/ProblemTagFilter/ProblemTagFilter';
import ProblemSetArchiveFilter from './problemsets/ProblemSetArchiveFilter/ProblemSetArchiveFilter';

import './ProblemsRoutes.scss';

export default function ProblemsRoutes({ children }) {
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
        {children}
      </ContentWithSidebar>
    </FullPageLayout>
  );
}
