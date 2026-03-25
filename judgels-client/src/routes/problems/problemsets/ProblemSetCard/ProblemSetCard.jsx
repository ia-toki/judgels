import { Flex } from '@blueprintjs/labs';

import { ContentCardLink } from '../../../../components/ContentCardLink/ContentCardLink';
import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { ProgressBar } from '../../../../components/ProgressBar/ProgressBar';
import { ProgressTag } from '../../../../components/ProgressTag/ProgressTag';

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
      <Flex asChild gap={2} justifyContent="space-between" alignItems="baseline">
        <h4>
          {problemSet.name}
          {renderProgress()}
        </h4>
      </Flex>
      {description && (
        <small>
          <HtmlText profilesMap={profilesMap}>{description}</HtmlText>
        </small>
      )}
      {renderProgressBar()}
    </ContentCardLink>
  );
}
