import * as React from 'react';

import { ContentCardLink } from 'components/ContentCardLink/ContentCardLink';
import { Course } from 'modules/api/jerahmeel/course';

import './ContestCard.css';

export interface CourseCardProps {
  course: Course;
}

export class ContestCard extends React.PureComponent<CourseCardProps> {
  render() {
    const { course } = this.props;

    return (
      <ContentCardLink to={`/contests/${course.slug}`}>
        <h4 className="contest-card-name">{course.name}</h4>
      </ContentCardLink>
    );
  }
}
