import { Tag } from '@blueprintjs/core';
import * as React from 'react';

import { ContentCardLink } from '../../../../../../../../components/ContentCardLink/ContentCardLink';
import { Course } from '../../../../../../../../modules/api/jerahmeel/course';
import { CourseChapter } from '../../../../../../../../modules/api/jerahmeel/courseChapter';
import { ChapterProblem } from '../../../../../../../../modules/api/jerahmeel/chapterProblem';
import { ProblemProgress } from '../../../../../../../../modules/api/jerahmeel/problem';
import { VerdictCode, getVerdictIntent } from '../../../../../../../../modules/api/gabriel/verdict';

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
        <div data-key="name">
          {problem.alias}. {problemName}
          {this.renderProgress()}
        </div>
      </ContentCardLink>
    );
  }

  private renderProgress = () => {
    const { progress } = this.props;
    if (!progress || progress.verdict === VerdictCode.PND) {
      return null;
    }

    const { verdict, score } = progress;
    const intent = getVerdictIntent(verdict);
    return (
      <div className="chapter-problem-card__progress">
        <Tag intent={intent}>{verdict}</Tag>
      </div>
    );
  };
}
