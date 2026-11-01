import { ContentRow } from '../../../../components/ContentRow/ContentRow';
import { ContentRowProgress } from '../../../../components/ContentRow/ContentRowProgress';
import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { ProgressTag } from '../../../../components/ProgressTag/ProgressTag';

import './CourseCard.scss';

function splitDescription(description) {
  const { body } = new DOMParser().parseFromString(description || '', 'text/html');

  const image = body.querySelector('img');
  image?.remove();
  body.querySelectorAll('[style]').forEach(element => element.removeAttribute('style'));

  return { imageUrl: image?.getAttribute('src'), text: body.innerHTML };
}

export function CourseCard({ course: { slug, name, description }, progress }) {
  const { imageUrl, text } = splitDescription(description);

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
      to={`/courses/${slug}`}
      leading={imageUrl && <img className="course-card__image" src={imageUrl} alt="" />}
      title={name}
      description={text && <HtmlText>{text}</HtmlText>}
      status={renderProgress()}
    />
  );
}
