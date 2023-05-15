import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { ProgressTag } from '../../../../components/ProgressTag/ProgressTag';
import { ProgressBar } from '../../../../components/ProgressBar/ProgressBar';
import { ContentCardLink } from '../../../../components/ContentCardLink/ContentCardLink';

import './ProblemSetCard.scss';

export function ProblemSetCard({ problemSet, archiveDescription, progress, profilesMap }) {
  const description = (archiveDescription || '') + (problemSet.description || '');

  const renderProgress = () => {
    if (!progress || progress.totalProblems === 0) {
      return null;
    }
    const { score, totalProblems } = progress;
    return (
      <ProgressTag num={score} denom={100 * totalProblems}>
        {score} pts / {totalProblems}
      </ProgressTag>
    );
  };

  const renderProgressBar = () => {
    if (!progress || progress.totalProblems === 0) {
      return null;
    }
    const { score, totalProblems } = progress;
    return <ProgressBar num={score} denom={100 * totalProblems} />;
  };

  return (
    <ContentCardLink to={`/problems/${problemSet.slug}`}>
      <h4 className="problemset-card__name">
        {problemSet.name}
        {renderProgress()}
      </h4>
      {description && (
        <small>
          <HtmlText profilesMap={profilesMap}>{description}</HtmlText>
        </small>
      )}
      {renderProgressBar()}
    </ContentCardLink>
  );
}
