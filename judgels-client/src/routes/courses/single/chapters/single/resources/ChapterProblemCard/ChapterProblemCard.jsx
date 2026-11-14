import { Code, Form } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import classNames from 'classnames';

import { ContentRow } from '../../../../../../../components/ContentRow/ContentRow';
import { VerdictCode } from '../../../../../../../modules/api/gradingVerdict';
import { ProblemType } from '../../../../../../../modules/api/problem';
import { ChapterProblemProgressTag } from '../ChapterProblemProgressTag/ChapterProblemProgressTag';

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
    return <span>{problemSetProblemPaths.map(p => p.join('/')).join(', ')}</span>;
  };

  const renderProgress = () => {
    if (!progress || progress.verdict === VerdictCode.PND) {
      return null;
    }

    const { verdict } = progress;
    return <ChapterProblemProgressTag verdict={verdict} />;
  };

  const renderStatus = () => {
    const paths = renderProblemSetProblemPaths();
    const progressTag = renderProgress();
    if (!paths && !progressTag) {
      return null;
    }

    return (
      <Flex alignItems="center" gap={2}>
        {paths}
        {progressTag}
      </Flex>
    );
  };

  return (
    <ContentRow
      className={classNames('chapter-problem-card', { 'chapter-problem-card--future': isFuture })}
      to={`/courses/${course.slug}/chapters/${chapterAlias}/problems/${problem.alias}`}
      leading={problem.type === ProblemType.Programming ? <Code /> : <Form />}
      title={`${problem.alias}. ${problemName}`}
      status={renderStatus()}
    />
  );
}
