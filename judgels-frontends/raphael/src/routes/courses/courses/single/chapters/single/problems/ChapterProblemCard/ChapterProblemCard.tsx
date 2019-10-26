import * as React from 'react';

import { ContentCardLink } from '../../../../../../../../components/ContentCardLink/ContentCardLink';
import { Course } from '../../../../../../../../modules/api/jerahmeel/course';
import { CourseChapter } from '../../../../../../../../modules/api/jerahmeel/courseChapter';
import { ChapterProblem } from '../../../../../../../../modules/api/jerahmeel/chapterProblem';

export interface ChapterProblemCardProps {
  course: Course;
  chapter: CourseChapter;
  problem: ChapterProblem;
  problemName: string;
}

export class ChapterProblemCard extends React.PureComponent<ChapterProblemCardProps> {
  render() {
    const { course, chapter, problem, problemName } = this.props;

    return (
      <ContentCardLink to={`/courses/${course.slug}/chapters/${chapter.alias}/problems/${problem.alias}`}>
        <span data-key="name">
          {problem.alias}. {problemName}
        </span>
      </ContentCardLink>
    );
  }
}
