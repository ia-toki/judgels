import * as React from 'react';

import { ContentCardLink } from '../../../../../../../../components/ContentCardLink/ContentCardLink';
import { Course } from '../../../../../../../../modules/api/jerahmeel/course';
import { CourseChapter } from '../../../../../../../../modules/api/jerahmeel/courseChapter';
import { ChapterLesson } from '../../../../../../../../modules/api/jerahmeel/chapterLesson';

export interface ChapterLessonCardProps {
  course: Course;
  chapter: CourseChapter;
  lesson: ChapterLesson;
  lessonName: string;
}

export class ChapterLessonCard extends React.PureComponent<ChapterLessonCardProps> {
  render() {
    const { course, chapter, lesson, lessonName } = this.props;

    return (
      <ContentCardLink to={`/courses/${course.slug}/chapters/${chapter.alias}/lessons/${lesson.alias}`}>
        <span data-key="name">
          {lesson.alias}. {lessonName}
        </span>
      </ContentCardLink>
    );
  }
}
