import { Manual, PanelStats } from '@blueprintjs/icons';
import { Outlet } from '@tanstack/react-router';

import ContentWithSidebar from '../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import ProblemTagFilter from '../../../components/ProblemTagFilter/ProblemTagFilter';
import { trainingProblemTagsQueryOptions } from '../../../modules/queries/trainingProblemTag';
import ProblemSetArchiveFilter from './ProblemSetArchiveFilter/ProblemSetArchiveFilter';

const TrainingProblemTagFilter = () => <ProblemTagFilter queryOptions={trainingProblemTagsQueryOptions()} />;

export default function ProblemSetsLayout() {
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

  return (
    <FullPageLayout>
      <ContentWithSidebar title="Menu" items={sidebarItems} basePath="/problems">
        <Outlet />
      </ContentWithSidebar>
    </FullPageLayout>
  );
}
