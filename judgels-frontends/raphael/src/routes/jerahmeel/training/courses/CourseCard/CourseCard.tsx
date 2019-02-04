import * as React from 'react';

import { ContentCardLink } from 'components/ContentCardLink/ContentCardLink';
import { Course } from 'modules/api/jerahmeel/course';

import './CourseCard.css';

export interface CourseCardProps {
  course: Course;
}

export class CourseCard extends React.PureComponent<CourseCardProps> {
  render() {
    const { course } = this.props;

    return (
      <ContentCardLink to={`/training/course/${course.id}`}>
        <h4 className="course-card-name">{`${course.id}. ${course.name}`}</h4>
      </ContentCardLink>
    );
  }
}
