import { Outlet, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import ContentWithTopbar from '../../../../../../components/ContentWithTopbar/ContentWithTopbar';

export default function ContestSettingsLayout() {
  const { contestSlug } = useParams({ strict: false });

  const topbarItems = [
    {
      path: 'general',
      title: 'General',
    },
    {
      path: 'modules',
      title: 'Modules',
    },
  ];

  return (
    <ContentCard title="Settings">
      <ContentWithTopbar items={topbarItems} basePath={`/contests/${contestSlug}/settings`}>
        <Outlet />
      </ContentWithTopbar>
    </ContentCard>
  );
}
