import * as React from 'react';

import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { ProgressTag } from '../../../../components/ProgressTag/ProgressTag';
import { ProgressBar } from '../../../../components/ProgressBar/ProgressBar';
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
      <ContentCardLink to={`/courses/${slug}`} className="course-card" elevation={1}>
        <h4>
          {`${name}`}
          {this.renderProgress()}
        </h4>
        {description && (
          <div className="course-card__description">
            <HtmlText>{description}</HtmlText>
          </div>
        )}
        {this.renderProgressBar()}
      </ContentCardLink>
    );
  }

  private renderProgress = () => {
    const { progress } = this.props;
    if (!progress || progress.totalChapters === 0) {
      return null;
    }

    const { solvedChapters, totalChapters } = progress;
    return (
      <ProgressTag num={solvedChapters} denom={totalChapters}>
        {solvedChapters} / {totalChapters} completed
      </ProgressTag>
    );
  };

  private renderProgressBar = () => {
    const { progress } = this.props;
    if (!progress) {
      return null;
    }
    return <ProgressBar num={progress.solvedChapters} denom={progress.totalChapters} />;
  };
}
