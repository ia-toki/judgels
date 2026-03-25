import { Intent, Tag } from '@blueprintjs/core';
import { SmallTick } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';

import { ProblemType } from '../../modules/api/sandalphon/problem';
import { ContentCardLink } from '../ContentCardLink/ContentCardLink';
import ProblemDifficulty from '../ProblemDifficulty/ProblemDifficulty';
import ProblemTopicsTags from '../ProblemTopicTags/ProblemTopicTags';
import { ProgressBar } from '../ProgressBar/ProgressBar';
import { VerdictProgressTag } from '../VerdictProgressTag/VerdictProgressTag';

export function ProblemSetProblemCard({ problemSet, problem, showAlias, problemName, metadata, difficulty, progress }) {
  const renderDifficulty = () => {
    return <ProblemDifficulty problem={problem} difficulty={difficulty} />;
  };

  const renderMetadata = () => {
    return renderTopicTags();
  };

  const renderEditorialTag = () => {
    if (!metadata.hasEditorial) {
      return null;
    }

    return (
      <Tag round intent={Intent.WARNING} className="problemset-problem-card__editorial-tag">
        editorial <SmallTick />
      </Tag>
    );
  };

  const renderTopicTags = () => {
    return <ProblemTopicsTags tags={metadata.tags} />;
  };

  const renderSpoilers = () => {
    return (
      <Flex justifyContent="space-between">
        {renderDifficulty()}
        {renderMetadata()}
      </Flex>
    );
  };

  const renderProgress = () => {
    if (problem.type === ProblemType.Bundle || !progress) {
      return null;
    }

    const { verdict, score } = progress;
    return <VerdictProgressTag verdict={verdict} score={score} />;
  };

  const renderProgressBar = () => {
    if (problem.type === ProblemType.Bundle || !progress) {
      return null;
    }
    return <ProgressBar verdict={progress.verdict} num={progress.score} denom={100} />;
  };

  return (
    <ContentCardLink to={`/problems/${problemSet.slug}/${problem.alias}`} className="problemset-problem-cardd">
      <h4 data-key="name">
        <Flex justifyContent="space-between">
          <span>
            {showAlias && <>{problem.alias}. </>}
            {problemName}
          </span>
          <Flex gap={1}>
            {renderEditorialTag()}
            {renderProgress()}
          </Flex>
        </Flex>
      </h4>
      {renderProgressBar()}
      {renderSpoilers()}
    </ContentCardLink>
  );
}
