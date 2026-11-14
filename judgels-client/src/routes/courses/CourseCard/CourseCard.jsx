import { ContentRow } from '../../../components/ContentRow/ContentRow';
import { ContentRowProgress } from '../../../components/ContentRow/ContentRowProgress';
import { HtmlText } from '../../../components/HtmlText/HtmlText';
import { ProgressTag } from '../../../components/ProgressTag/ProgressTag';
import { splitCourseDescription } from '../splitCourseDescription';

import './CourseCard.scss';

export function CourseCard({ course: { slug, name, description }, progress }) {
  const { imageUrl, text } = splitCourseDescription(description);

  const renderProgress = () => {
    if (!progress || progress.solvedProblems === 0) {
      return null;
    }

    const { solvedProblems, totalProblems } = progress;

    return (
      <ContentRowProgress num={solvedProblems} denom={totalProblems}>
        <ProgressTag num={solvedProblems} denom={totalProblems}>
          {solvedProblems} / {totalProblems} problems
        </ProgressTag>
      </ContentRowProgress>
    );
  };

  return (
    <ContentRow
      className="course-card"
      to={`/courses/${slug}`}
      leading={imageUrl && <img className="course-card__image" src={imageUrl} alt="" />}
      title={name}
      description={text && <HtmlText>{text}</HtmlText>}
      status={renderProgress()}
    />
  );
}
