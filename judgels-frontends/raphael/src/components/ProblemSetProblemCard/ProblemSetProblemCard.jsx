import { Tag, Intent } from '@blueprintjs/core';
import { SmallTick } from '@blueprintjs/icons';

import { ContentCardLink } from '../ContentCardLink/ContentCardLink';
import ProblemDifficulty from '../ProblemDifficulty/ProblemDifficulty';
import ProblemTopicsTags from '../ProblemTopicTags/ProblemTopicTags';
import { VerdictProgressTag } from '../VerdictProgressTag/VerdictProgressTag';
import { ProgressBar } from '../ProgressBar/ProgressBar';
import { ProblemType } from '../../modules/api/sandalphon/problem';

import './ProblemSetProblemCard.scss';

export function ProblemSetProblemCard({ problemSet, problem, showAlias, problemName, metadata, difficulty, progress }) {
  const renderDifficulty = () => {
    return (
      <div className="float-left">
        <ProblemDifficulty problem={problem} difficulty={difficulty} />
      </div>
    );
  };

  const renderMetadata = () => {
    return <div className="float-right">{renderTopicTags()}</div>;
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
      <>
        {renderDifficulty()}
        {renderMetadata()}
      </>
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
    <ContentCardLink to={`/problems/${problemSet.slug}/${problem.alias}`} className="problemset-problem-card">
      <h4 data-key="name">
        {showAlias && <>{problem.alias}. </>}
        {problemName}
        {renderProgress()}
        {renderEditorialTag()}
      </h4>
      {renderProgressBar()}
      {renderSpoilers()}
    </ContentCardLink>
  );
}
