import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { curriculumByJidQueryOptions } from '../../../../modules/queries/curriculum';
import { CurriculumGeneralSection } from '../CurriculumGeneralSection/CurriculumGeneralSection';

export default function CurriculumPage() {
  const { curriculumJid } = useParams({ strict: false });

  const { data: curriculum } = useSuspenseQuery(curriculumByJidQueryOptions(curriculumJid));

  return (
    <ContentCard title={`Curriculum › ${curriculum.name}`}>
      <CurriculumGeneralSection curriculum={curriculum} />
    </ContentCard>
  );
}
