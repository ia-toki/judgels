import { Code, Form } from '@blueprintjs/icons';
import classNames from 'classnames';

import { ContentCardLink } from '../../../../../../../../components/ContentCardLink/ContentCardLink';
import { ProgressBar } from '../../../../../../../../components/ProgressBar/ProgressBar';
import { ChapterProblemProgressTag } from '../../../../../../../../components/VerdictProgressTag/ChapterProblemProgressTag';
import { VerdictCode } from '../../../../../../../../modules/api/gabriel/verdict';
import { ProblemType } from '../../../../../../../../modules/api/sandalphon/problem';

import './ChapterProblemCard.scss';

export function ChapterProblemCard({
  course,
  chapter,
  problem,
  problemName,
  problemSetProblemPaths,
  progress,
  isFuture,
}) {
  const renderProblemSetProblemPaths = () => {
    if (!problemSetProblemPaths) {
      return null;
    }
    return (
      <div className="chapter-problem-card__problem-set-problem-paths">
        {problemSetProblemPaths.map(p => p.join('/')).join(', ')}
      </div>
    );
  };

  const renderProgress = () => {
    if (!progress) {
      return null;
    }

    const { verdict } = progress;
    return <ChapterProblemProgressTag className="chapter-problem-card__progress" verdict={verdict} />;
  };

  return (
    <ContentCardLink
      className={classNames('chapter-problem-card', { 'chapter-problem-card--future': isFuture })}
      to={`/courses/${course.slug}/chapters/${chapter.alias}/problems/${problem.alias}`}
    >
      <div className="chapter-problem-card__heading">
        {problem.type === ProblemType.Programming ? <Code /> : <Form />}
        <h4 data-key="name">
          {problem.alias}. {problemName}
        </h4>
        {renderProblemSetProblemPaths()}
        {renderProgress()}
      </div>
    </ContentCardLink>
  );
}
