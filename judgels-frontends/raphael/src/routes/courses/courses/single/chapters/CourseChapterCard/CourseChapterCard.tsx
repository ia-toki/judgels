import { Intent, Tag, ProgressBar } from '@blueprintjs/core';
import * as React from 'react';

import { ContentCardLink } from '../../../../../../components/ContentCardLink/ContentCardLink';
import { Course } from '../../../../../../modules/api/jerahmeel/course';
import { ChapterProgress } from '../../../../../../modules/api/jerahmeel/chapter';
import { CourseChapter } from '../../../../../../modules/api/jerahmeel/courseChapter';

import './CourseChapterCard.css';

export interface CourseChapterCardProps {
  course: Course;
  chapter: CourseChapter;
  chapterName: string;
  progress: ChapterProgress;
}

export class CourseChapterCard extends React.PureComponent<CourseChapterCardProps> {
  render() {
    const { course, chapter, chapterName } = this.props;

    return (
      <ContentCardLink to={`/courses/${course.slug}/chapters/${chapter.alias}`} className="couse-chapter-card">
        <div data-key="name">
          <h4>
            {chapter.alias}. {chapterName}
            {this.renderProgress()}
          </h4>
        </div>
        {this.renderProgressBar()}
      </ContentCardLink>
    );
  }

  private renderProgress = () => {
    const { progress } = this.props;
    if (!progress || progress.totalProblems === 0) {
      return null;
    }

    const { solvedProblems, totalProblems } = progress;

    let intent: Intent;
    if (solvedProblems === totalProblems) {
      intent = Intent.SUCCESS;
    } else if (solvedProblems > 0) {
      intent = Intent.WARNING;
    } else {
      intent = Intent.NONE;
    }
    return (
      <div className="course-chapter-card__progress">
        <Tag intent={intent}>
          {solvedProblems} / {totalProblems} problems solved
        </Tag>
      </div>
    );
  };

  private renderProgressBar = () => {
    const { progress } = this.props;
    if (!progress || progress.totalProblems === 0) {
      return null;
    }

    const { solvedProblems, totalProblems } = progress;

    let intent: Intent;
    if (solvedProblems === totalProblems) {
      intent = Intent.SUCCESS;
    } else if (solvedProblems > 0) {
      intent = Intent.WARNING;
    } else {
      intent = Intent.NONE;
    }
    return (
      <ProgressBar
        animate={false}
        stripes={solvedProblems < totalProblems}
        intent={intent}
        value={solvedProblems / totalProblems}
      />
    );
  };
}
