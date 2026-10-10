import { Outlet, useParams } from '@tanstack/react-router';

import ContentWithTopbar from '../../../../components/ContentWithTopbar/ContentWithTopbar';

const topbarItems = [
  { path: 'engine', title: 'Engine' },
  { path: 'config', title: 'Config' },
  { path: 'test-data', title: 'Test data' },
  { path: 'helpers', title: 'Helpers' },
  { path: 'languages', title: 'Languages' },
];

export default function ProblemGradingLayout() {
  const { problemJid } = useParams({ strict: false });

  return (
    <ContentWithTopbar items={topbarItems} basePath={`/admin/problems/${problemJid}/grading`}>
      <Outlet />
    </ContentWithTopbar>
  );
}
