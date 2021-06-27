import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { ProgressTag } from '../../../../components/ProgressTag/ProgressTag';
import { ProgressBar } from '../../../../components/ProgressBar/ProgressBar';
import { ContentCardLink } from '../../../../components/ContentCardLink/ContentCardLink';

export function CourseCard({ course: { slug, name, description }, progress }) {
  const renderProgress = () => {
    if (!progress || progress.totalChapters === 0) {
      return null;
    }

    const { solvedChapters, totalSolvableChapters } = progress;

    return (
      <ProgressTag num={solvedChapters} denom={totalSolvableChapters}>
        {solvedChapters} / {totalSolvableChapters} completed
      </ProgressTag>
    );
  };

  const renderProgressBar = () => {
    if (!progress) {
      return null;
    }
    return <ProgressBar num={progress.solvedChapters} denom={progress.totalSolvableChapters} />;
  };

  return (
    <ContentCardLink to={`/courses/${slug}`} className="course-card">
      <h4>
        {`${name}`}
        {renderProgress()}
      </h4>
      {description && (
        <small className="course-card__description">
          <HtmlText>{description}</HtmlText>
        </small>
      )}
      {renderProgressBar()}
    </ContentCardLink>
  );
}
