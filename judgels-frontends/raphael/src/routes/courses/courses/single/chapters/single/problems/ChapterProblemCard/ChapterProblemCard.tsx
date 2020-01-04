import * as React from 'react';

import { ContentCardLink } from '../../../../../../../../components/ContentCardLink/ContentCardLink';
import { VerdictProgressTag } from '../../../../../../../../components/VerdictProgressTag/VerdictProgressTag';
import { ProgressBar } from '../../../../../../../../components/ProgressBar/ProgressBar';
import { ProblemType } from '../../../../../../../../modules/api/sandalphon/problem';
import { Course } from '../../../../../../../../modules/api/jerahmeel/course';
import { CourseChapter } from '../../../../../../../../modules/api/jerahmeel/courseChapter';
import { ChapterProblem } from '../../../../../../../../modules/api/jerahmeel/chapterProblem';
import { ProblemProgress } from '../../../../../../../../modules/api/jerahmeel/problem';
import { VerdictCode } from '../../../../../../../../modules/api/gabriel/verdict';

import './ChapterProblemCard.css';

export interface ChapterProblemCardProps {
  course: Course;
  chapter: CourseChapter;
  problem: ChapterProblem;
  problemName: string;
  progress: ProblemProgress;
}

export class ChapterProblemCard extends React.PureComponent<ChapterProblemCardProps> {
  render() {
    const { course, chapter, problem, problemName } = this.props;

    return (
      <ContentCardLink to={`/courses/${course.slug}/chapters/${chapter.alias}/problems/${problem.alias}`}>
        <div data-key="name" className="chapter-problem-card__name">
          {problem.alias}. {problemName}
          {this.renderProgress()}
        </div>
        {this.renderProgressBar()}
      </ContentCardLink>
    );
  }

  private renderProgress = () => {
    const { problem, progress } = this.props;
    if (problem.type === ProblemType.Bundle || !progress || progress.verdict === VerdictCode.PND) {
      return null;
    }

    const { verdict, score } = progress;
    return <VerdictProgressTag className="chapter-problem-card__progress" verdict={verdict} score={score} />;
  };

  private renderProgressBar = () => {
    const { problem, progress } = this.props;
    if (problem.type === ProblemType.Bundle || !progress) {
      return null;
    }
    return <ProgressBar num={progress.score} denom={100} />;
  };
}
