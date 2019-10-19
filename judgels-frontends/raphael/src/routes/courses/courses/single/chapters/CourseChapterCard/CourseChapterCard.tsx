import * as React from 'react';

import { ContentCardLink } from '../../../../../../components/ContentCardLink/ContentCardLink';
import { Course } from '../../../../../../modules/api/jerahmeel/course';
import { CourseChapter } from '../../../../../../modules/api/jerahmeel/courseChapter';

export interface CourseChapterCardProps {
  course: Course;
  chapter: CourseChapter;
  chapterName: string;
}

export class CourseChapterCard extends React.PureComponent<CourseChapterCardProps> {
  render() {
    const { course, chapter, chapterName } = this.props;

    return (
      <ContentCardLink to={`/courses/${course.slug}/chapters/${chapter.alias}`}>
        <span data-key="name">
          {chapter.alias}. {chapterName}
        </span>
      </ContentCardLink>
    );
  }
}
