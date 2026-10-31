import { ContentRow } from '../../../../components/ContentRow/ContentRow';
import { ContentRowProgress } from '../../../../components/ContentRow/ContentRowProgress';
import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { ProgressTag } from '../../../../components/ProgressTag/ProgressTag';

export function ProblemSetCard({ problemSet, archiveDescription, progress, profilesMap }) {
  const description = (archiveDescription || '') + (problemSet.description || '');

  const renderProgress = () => {
    if (!progress || progress.score === 0) {
      return null;
    }
    const { score, totalProblems } = progress;
    return (
      <ContentRowProgress num={score} denom={100 * totalProblems}>
        <ProgressTag num={score} denom={100 * totalProblems}>
          {score} pts / {totalProblems}
        </ProgressTag>
      </ContentRowProgress>
    );
  };

  return (
    <ContentRow
      to={`/problems/${problemSet.slug}`}
      title={problemSet.name}
      meta={description && <HtmlText profilesMap={profilesMap}>{description}</HtmlText>}
      status={renderProgress()}
    />
  );
}
