import { Intent, Tag } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';

import { VerdictCode } from '../../modules/api/gradingVerdict';
import { ProblemType } from '../../modules/api/problem';
import { ContentRow } from '../ContentRow/ContentRow';
import { ContentRowProgress } from '../ContentRow/ContentRowProgress';
import { ProblemDifficultyTags } from '../ProblemDifficulty/ProblemDifficulty';
import { ProblemTopicTagList } from '../ProblemTopicTags/ProblemTopicTags';
import { VerdictProgressTag } from '../VerdictProgressTag/VerdictProgressTag';

export function ProblemSetProblemCard({ problemSet, problem, showAlias, problemName, metadata, difficulty, progress }) {
  const renderEditorialTag = () => {
    if (!metadata.hasEditorial) {
      return null;
    }

    return (
      <Tag round intent={Intent.WARNING}>
        editorial
      </Tag>
    );
  };

  const renderProgress = () => {
    if (problem.type === ProblemType.Bundle || !progress || progress.verdict === VerdictCode.PND) {
      return null;
    }

    const { verdict, score } = progress;
    return (
      <ContentRowProgress verdict={verdict} num={score} denom={100}>
        <VerdictProgressTag verdict={verdict} score={score} />
      </ContentRowProgress>
    );
  };

  const renderStatus = () => {
    const editorialTag = renderEditorialTag();
    const progressBar = renderProgress();
    if (!editorialTag && !progressBar) {
      return null;
    }

    return (
      <Flex alignItems="start" gap={2}>
        {editorialTag}
        {progressBar || <div className="content-row__progress-space" />}
      </Flex>
    );
  };

  return (
    <ContentRow
      to={`/problems/${problemSet.slug}/${problem.alias}`}
      title={showAlias ? `${problem.alias}. ${problemName}` : problemName}
      badges={
        <>
          <ProblemDifficultyTags problem={problem} difficulty={difficulty} />
          <ProblemTopicTagList tags={metadata.tags} />
        </>
      }
      status={renderStatus()}
    />
  );
}
