import { Tag, Intent } from '@blueprintjs/core';
import * as React from 'react';

import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { ContentCardLink } from '../../../../components/ContentCardLink/ContentCardLink';
import { Course, CourseProgress } from '../../../../modules/api/jerahmeel/course';

import './CourseCard.css';

export interface CourseCardProps {
  course: Course;
  progress: CourseProgress;
}

export class CourseCard extends React.PureComponent<CourseCardProps> {
  render() {
    const { slug, name, description } = this.props.course;

    return (
      <ContentCardLink to={`/courses/${slug}`} className="course-card">
        <h4>
          {`${name}`}
          {this.renderProgress()}
        </h4>
        {description && (
          <div className="course-card__description">
            <HtmlText>{description}</HtmlText>
          </div>
        )}
      </ContentCardLink>
    );
  }

  private renderProgress = () => {
    const { progress } = this.props;
    if (!progress || progress.totalChapters === 0) {
      return null;
    }

    const { solvedChapters, totalChapters } = progress;

    let intent: Intent;
    if (solvedChapters === totalChapters) {
      intent = Intent.SUCCESS;
    } else if (solvedChapters > 0) {
      intent = Intent.WARNING;
    } else {
      intent = Intent.NONE;
    }
    return (
      <div className="course-card__progress">
        <Tag intent={intent}>
          {solvedChapters} / {totalChapters} chapters completed
        </Tag>
      </div>
    );
  };
}
