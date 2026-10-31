import { ContentTile } from '../../../../components/ContentTile/ContentTile';
import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { ProgressBar } from '../../../../components/ProgressBar/ProgressBar';

export function CourseCard({ course: { slug, name, description }, progress }) {
  const renderProgress = () => {
    if (!progress || progress.solvedProblems === 0) {
      return null;
    }

    const { solvedProblems, totalProblems } = progress;

    return (
      <>
        <ProgressBar num={solvedProblems} denom={totalProblems} />
        <span>
          {solvedProblems} / {totalProblems} problems
        </span>
      </>
    );
  };

  return (
    <ContentTile
      to={`/courses/${slug}`}
      title={name}
      description={description && <HtmlText>{description}</HtmlText>}
      footer={renderProgress()}
    />
  );
}
