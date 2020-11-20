import * as React from 'react';

import { ContentCardLink } from '../../../../../../../../components/ContentCardLink/ContentCardLink';

export function ChapterLessonCard({ course, chapter, lesson, lessonName }) {
  return (
    <ContentCardLink to={`/courses/${course.slug}/chapters/${chapter.alias}/lessons/${lesson.alias}`}>
      <span data-key="name">
        {lesson.alias}. {lessonName}
      </span>
    </ContentCardLink>
  );
}
