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
      <ChapterGeneralSection chapter={chapter} />
      <hr />
      <ChapterLessonsSection chapter={chapter} />
      <hr />
      <ChapterProblemsSection chapter={chapter} />
    </ContentCard>
  );
}
