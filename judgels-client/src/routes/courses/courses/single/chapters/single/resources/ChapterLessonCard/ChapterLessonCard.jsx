import { Book } from '@blueprintjs/icons';

import { ContentCardLink } from '../../../../../../../../components/ContentCardLink/ContentCardLink';

import './ChapterLessonCard.scss';

export function ChapterLessonCard({ course, chapterAlias, lesson, lessonName }) {
  return (
    <ContentCardLink
      className="chapter-lesson-card"
      to={`/courses/${course.slug}/chapters/${chapterAlias}/lessons/${lesson.alias}`}
    >
      <Book />
      <h4 data-key="name">
        {lesson.alias}. {lessonName}
      </h4>
    </ContentCardLink>
  );
}
