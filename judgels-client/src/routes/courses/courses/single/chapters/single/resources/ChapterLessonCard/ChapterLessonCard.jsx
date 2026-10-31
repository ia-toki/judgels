import { Book } from '@blueprintjs/icons';

import { ContentRow } from '../../../../../../../../components/ContentRow/ContentRow';

export function ChapterLessonCard({ course, chapterAlias, lesson, lessonName }) {
  return (
    <ContentRow
      to={`/courses/${course.slug}/chapters/${chapterAlias}/lessons/${lesson.alias}`}
      leading={<Book />}
      title={`${lesson.alias}. ${lessonName}`}
    />
  );
}
