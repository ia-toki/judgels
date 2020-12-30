import { ContentCardLink } from '../../../../../../../../components/ContentCardLink/ContentCardLink';
import { VerdictProgressTag } from '../../../../../../../../components/VerdictProgressTag/VerdictProgressTag';
import { ProgressBar } from '../../../../../../../../components/ProgressBar/ProgressBar';
import { ProblemType } from '../../../../../../../../modules/api/sandalphon/problem';

import './ChapterProblemCard.css';

export function ChapterProblemCard({ course, chapter, problem, progress, problemName }) {
  const renderProgress = () => {
    if (problem.type === ProblemType.Bundle || !progress) {
      return null;
    }

    const { verdict, score } = progress;
    return <VerdictProgressTag className="chapter-problem-card__progress" verdict={verdict} score={score} />;
  };

  const renderProgressBar = () => {
    if (problem.type === ProblemType.Bundle || !progress) {
      return null;
    }
    return <ProgressBar verdict={progress.verdict} num={progress.score} denom={100} />;
  };

  return (
    <ContentCardLink
      className="chapter-problem-card"
      to={`/courses/${course.slug}/chapters/${chapter.alias}/problems/${problem.alias}`}
      elevation={1}
    >
      <h4 data-key="name">
        {problem.alias}. {problemName}
        {renderProgress()}
      </h4>
      {renderProgressBar()}
    </ContentCardLink>
  );
}
