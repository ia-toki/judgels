import { Flex } from '@blueprintjs/labs';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { archivesQueryOptions } from '../../../../modules/queries/archive';
import { problemSetBySlugQueryOptions } from '../../../../modules/queries/problemSet';
import { ProblemSetGeneralSection } from '../ProblemSetGeneralSection/ProblemSetGeneralSection';
import { ProblemSetProblemsSection } from '../ProblemSetProblemsSection/ProblemSetProblemsSection';

export default function ProblemSetPage() {
  const { problemSetSlug } = useParams({ strict: false });

  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: archivesResponse } = useSuspenseQuery(archivesQueryOptions());

  const archiveSlug = archivesResponse.data.find(a => a.jid === problemSet.archiveJid)?.slug;

  return (
    <ContentCard title={<BreadcrumbTitle parent="Problemsets" current={problemSet.slug} />}>
      <Flex flexDirection="column" gap={8}>
        <ProblemSetGeneralSection problemSet={problemSet} archiveSlug={archiveSlug} />
        <ProblemSetProblemsSection problemSet={problemSet} />
      </Flex>
    </ContentCard>
  );
}
