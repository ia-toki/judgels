import { Flex } from '@blueprintjs/labs';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { chapterByJidQueryOptions } from '../../../../modules/queries/chapter';
import { ChapterGeneralSection } from '../ChapterGeneralSection/ChapterGeneralSection';
import { ChapterLessonsSection } from '../ChapterLessonsSection/ChapterLessonsSection';
import { ChapterProblemsSection } from '../ChapterProblemsSection/ChapterProblemsSection';

export default function ChapterPage() {
  const { chapterJid } = useParams({ strict: false });

  const { data: chapter } = useSuspenseQuery(chapterByJidQueryOptions(chapterJid));

  return (
    <ContentCard title={`Chapters › ${chapter.name}`}>
      <Flex flexDirection="column" gap={8}>
        <ChapterGeneralSection chapter={chapter} />
        <ChapterLessonsSection chapter={chapter} />
        <ChapterProblemsSection chapter={chapter} />
      </Flex>
    </ContentCard>
  );
}
