import { Manual, PanelStats } from '@blueprintjs/icons';
import { Outlet } from '@tanstack/react-router';

import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullWidthPageLayout } from '../../components/FullWidthPageLayout/FullWidthPageLayout';
import ProblemTagFilter from '../../components/ProblemTagFilter/ProblemTagFilter';
import { trainingProblemTagsQueryOptions } from '../../modules/queries/trainingProblemTag';
import ProblemSetArchiveFilter from './problemsets/ProblemSetArchiveFilter/ProblemSetArchiveFilter';

const TrainingProblemTagFilter = () => <ProblemTagFilter queryOptions={trainingProblemTagsQueryOptions()} />;

export default function ProblemsIndexLayout() {
  const sidebarItems = [
    {
      path: '',
      titleIcon: <Manual />,
      title: 'Browse problems',
      widgetComponent: TrainingProblemTagFilter,
    },
    {
      path: 'problemsets',
      titleIcon: <PanelStats />,
      title: 'Browse problemsets',
      widgetComponent: ProblemSetArchiveFilter,
    },
  ];

  const contentWithSidebarProps = {
    title: 'Menu',
    items: sidebarItems,
    basePath: '/problems',
  };

  return (
    <FullWidthPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Outlet />
      </ContentWithSidebar>
    </FullWidthPageLayout>
  );
}
