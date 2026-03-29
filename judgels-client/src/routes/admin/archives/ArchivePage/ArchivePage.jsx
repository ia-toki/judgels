import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { archiveBySlugQueryOptions } from '../../../../modules/queries/archive';
import { ArchiveGeneralSection } from '../ArchiveGeneralSection/ArchiveGeneralSection';

export default function ArchivePage() {
  const { archiveSlug } = useParams({ strict: false });

  const { data: archive } = useSuspenseQuery(archiveBySlugQueryOptions(archiveSlug));

  return (
    <ContentCard title={`Archives › ${archive.slug}`}>
      <ArchiveGeneralSection archive={archive} />
    </ContentCard>
  );
}
