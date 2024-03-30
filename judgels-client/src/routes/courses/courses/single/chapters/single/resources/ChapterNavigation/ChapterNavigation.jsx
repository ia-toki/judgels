import { Intent } from '@blueprintjs/core';
import { ChevronLeft, ChevronRight } from '@blueprintjs/icons';

import { ButtonLink } from '../../../../../../../../components/ButtonLink/ButtonLink';

export function ChapterNavigation({
  courseSlug,
  chapterAlias,
  previousResourcePath,
  nextResourcePath,
  chapters,
  disableNext,
}) {
  const scrollToTop = () => {
    window.scrollTo(0, 0);
  };

  const renderPreviousResource = () => {
    if (!previousResourcePath) {
      return null;
    }
    return (
      <ButtonLink
        small
        className="chapter-problem-page__prev"
        to={`/courses/${courseSlug}/chapters/${chapterAlias}${previousResourcePath}`}
        onClick={scrollToTop}
      >
        <ChevronLeft />
        &nbsp;&nbsp;&nbsp;Prev
      </ButtonLink>
    );
  };

  const renderNextResource = () => {
    if (!nextResourcePath) {
      return null;
    }
    return (
      <ButtonLink
        small
        disabled={disableNext}
        intent={Intent.WARNING}
        to={`/courses/${courseSlug}/chapters/${chapterAlias}${nextResourcePath}`}
        onClick={scrollToTop}
      >
        Next &nbsp;&nbsp;
        <ChevronRight />
      </ButtonLink>
    );
  };

  const renderNextChapter = () => {
    if (nextResourcePath || !chapters) {
      return null;
    }

    for (let i = 0; i < chapters.length; i++) {
      if (chapters[i].alias === chapterAlias) {
        if (i + 1 < chapters.length) {
          return (
            <ButtonLink
              small
              disabled={disableNext}
              intent={Intent.WARNING}
              to={`/courses/${courseSlug}/chapters/${chapters[i + 1].alias}`}
            >
              Next chapter &nbsp;&nbsp;
              <ChevronRight />
            </ButtonLink>
          );
        }
      }
    }
    return null;
  };

  return (
    <div>
      {renderPreviousResource()}
      {renderNextResource()}
      {renderNextChapter()}
    </div>
  );
}
