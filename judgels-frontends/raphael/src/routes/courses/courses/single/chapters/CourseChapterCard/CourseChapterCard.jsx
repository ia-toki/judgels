import * as React from 'react';

import { ContentCardLink } from '../../../../../../components/ContentCardLink/ContentCardLink';
import { ProgressTag } from '../../../../../../components/ProgressTag/ProgressTag';
import { ProgressBar } from '../../../../../../components/ProgressBar/ProgressBar';
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
      <ContentCardLink
        to={`/courses/${course.slug}/chapters/${chapter.alias}`}
        className="couse-chapter-card"
        elevation={1}
      >
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
    return (
      <ProgressTag num={solvedProblems} denom={totalProblems}>
        {solvedProblems} / {totalProblems} solved
      </ProgressTag>
    );
  };

  private renderProgressBar = () => {
    const { progress } = this.props;
    if (!progress) {
      return null;
    }
    return <ProgressBar num={progress.solvedProblems} denom={progress.totalProblems} />;
  };
}
