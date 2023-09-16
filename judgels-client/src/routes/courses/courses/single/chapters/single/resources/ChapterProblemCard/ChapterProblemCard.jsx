import { Code, Form } from '@blueprintjs/icons';

import { ContentCardLink } from '../../../../../../../../components/ContentCardLink/ContentCardLink';
import { ProgressBar } from '../../../../../../../../components/ProgressBar/ProgressBar';
import { ChapterProblemProgressTag } from '../../../../../../../../components/VerdictProgressTag/ChapterProblemProgressTag';
import { ProblemType } from '../../../../../../../../modules/api/sandalphon/problem';

import './ChapterProblemCard.scss';

export function ChapterProblemCard({ course, chapter, problem, progress, problemName }) {
  const renderProgress = () => {
    if (problem.type === ProblemType.Bundle || !progress) {
      return null;
    }

    const { verdict } = progress;
    return <ChapterProblemProgressTag className="chapter-problem-card__progress" verdict={verdict} />;
  };

  const renderProgressBar = () => {
    if (problem.type === ProblemType.Bundle || !progress) {
      return null;
    }
    return <ProgressBar num={progress.score} denom={100} />;
  };

  return (
    <ContentCardLink
      className="chapter-problem-card"
      to={`/courses/${course.slug}/chapters/${chapter.alias}/problems/${problem.alias}`}
    >
      <div className="chapter-problem-card__heading">
        {problem.type === ProblemType.Programming ? <Code /> : <Form />}
        <h4 data-key="name">
          {problem.alias}. {problemName}
        </h4>
        {renderProgress()}
      </div>
      {renderProgressBar()}
    </ContentCardLink>
  );
}
