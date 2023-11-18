import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { ProgressTag } from '../../../../components/ProgressTag/ProgressTag';
import { ProgressBar } from '../../../../components/ProgressBar/ProgressBar';
import { ContentCardLink } from '../../../../components/ContentCardLink/ContentCardLink';

import './CourseCard.scss';

export function CourseCard({ course: { slug, name, description }, progress }) {
  const renderProgress = () => {
    if (!progress || progress.totalProblems === 0) {
      return null;
    }

    const { solvedProblems, totalProblems } = progress;

    return (
      <ProgressTag num={solvedProblems} denom={totalProblems}>
        {solvedProblems} / {totalProblems} problems completed
      </ProgressTag>
    );
  };

  const renderProgressBar = () => {
    if (!progress) {
      return null;
    }
    return (
      <ProgressBar className="course-card__progress-bar" num={progress.solvedProblems} denom={progress.totalProblems} />
    );
  };

  return (
    <ContentCardLink to={`/courses/${slug}`} className="course-card">
      <h4 className="course-card__title">
        {`${name}`}
        {renderProgress()}
      </h4>
      <hr />
      {description && (
        <small className="course-card__description">
          <HtmlText>{description}</HtmlText>
        </small>
      )}
      {renderProgressBar()}
    </ContentCardLink>
  );
}
