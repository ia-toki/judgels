import { Tag, Intent } from '@blueprintjs/core';
import { SmallTick } from '@blueprintjs/icons';

import { ContentCardLink } from '../ContentCardLink/ContentCardLink';
import ProblemDifficulty from '../ProblemDifficulty/ProblemDifficulty';
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
    const { hasEditorial } = metadata;
    if (!hasEditorial) {
      return null;
    }

    return (
      <div className="float-right">
        <Tag round intent={Intent.WARNING}>
          editorial <SmallTick />
        </Tag>
      </div>
    );
  };

  const renderSpoilers = () => {
    return (
      <div className="problemset-problem-card__spoilers">
        {renderDifficulty()}
        {renderMetadata()}
      </div>
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
      </h4>
      {renderProgressBar()}
      {renderSpoilers()}
    </ContentCardLink>
  );
}
