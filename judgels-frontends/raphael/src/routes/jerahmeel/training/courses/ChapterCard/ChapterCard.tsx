import * as React from 'react';

import { ContentCardLink } from 'components/ContentCardLink/ContentCardLink';
import { Chapter } from 'modules/api/jerahmeel/chapter';
import { Course } from 'modules/api/jerahmeel/course';

import './ChapterCard.css';

export interface ChapterCardProps {
  course: Course;
  chapter: Chapter;
}

export class ChapterCard extends React.PureComponent<ChapterCardProps> {
  render() {
    const { course, chapter } = this.props;

    return (
      <ContentCardLink to={`/training/course/${course.id}/chapter/${chapter.id}`}>
        <h4 className="chapter-card-name">{`${chapter.id}. ${chapter.name}`}</h4>
      </ContentCardLink>
    );
  }
}
