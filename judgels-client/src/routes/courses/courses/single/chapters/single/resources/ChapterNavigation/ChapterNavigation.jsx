import { Intent } from '@blueprintjs/core';
import { ChevronLeft, ChevronRight } from '@blueprintjs/icons';

import { ButtonLink } from '../../../../../../../../components/ButtonLink/ButtonLink';

export function ChapterNavigation({ courseSlug, chapterAlias, previousResourcePath, nextResourcePath, chapters }) {
  const renderPreviousResource = () => {
    if (!previousResourcePath) {
      return null;
    }
    return (
      <ButtonLink
        small
        className="chapter-problem-page__prev"
        to={`/courses/${courseSlug}/chapters/${chapterAlias}${previousResourcePath}`}
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
        intent={Intent.WARNING}
        to={`/courses/${courseSlug}/chapters/${chapterAlias}${nextResourcePath}`}
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
            <ButtonLink small intent={Intent.WARNING} to={`/courses/${courseSlug}/chapters/${chapters[i + 1].alias}`}>
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
