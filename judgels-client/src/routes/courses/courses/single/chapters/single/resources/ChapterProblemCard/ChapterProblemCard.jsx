import { Code, Form } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import classNames from 'classnames';

import { ContentRow } from '../../../../../../../../components/ContentRow/ContentRow';
import { ChapterProblemProgressTag } from '../../../../../../../../components/VerdictProgressTag/ChapterProblemProgressTag';
import { VerdictCode } from '../../../../../../../../modules/api/gradingVerdict';
import { ProblemType } from '../../../../../../../../modules/api/problem';

import './ChapterProblemCard.scss';

export function ChapterProblemCard({
  course,
  chapterAlias,
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
      <span className="chapter-problem-card__paths">{problemSetProblemPaths.map(p => p.join('/')).join(', ')}</span>
    );
  };

  const renderProgress = () => {
    if (!progress || progress.verdict === VerdictCode.PND) {
      return null;
    }

    const { verdict } = progress;
    return <ChapterProblemProgressTag verdict={verdict} />;
  };

  return (
    <ContentRow
      className={classNames({ 'chapter-problem-card--future': isFuture })}
      to={`/courses/${course.slug}/chapters/${chapterAlias}/problems/${problem.alias}`}
      leading={problem.type === ProblemType.Programming ? <Code /> : <Form />}
      title={`${problem.alias}. ${problemName}`}
      status={
        <Flex alignItems="center" gap={2}>
          {renderProblemSetProblemPaths()}
          {renderProgress()}
        </Flex>
      }
    />
  );
}
