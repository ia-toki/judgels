import * as React from 'react';

import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { ContentCardLink } from '../../../../components/ContentCardLink/ContentCardLink';
import { Course } from '../../../../modules/api/jerahmeel/course';

import './CourseCard.css';

export interface CourseCardProps {
  course: Course;
}

export class CourseCard extends React.PureComponent<CourseCardProps> {
  render() {
    const { course } = this.props;

    return (
      <ContentCardLink to={`/courses/${course.slug}`} className="course-card">
        <h3>{`${course.name}`}</h3>
        <hr />
        <HtmlText>{course.description}</HtmlText>
      </ContentCardLink>
    );
  }
}
