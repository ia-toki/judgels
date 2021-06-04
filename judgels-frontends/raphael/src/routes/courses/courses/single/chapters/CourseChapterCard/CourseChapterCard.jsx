import { ContentCardLink } from '../../../../../../components/ContentCardLink/ContentCardLink';
import { ProgressTag } from '../../../../../../components/ProgressTag/ProgressTag';
import { ProgressBar } from '../../../../../../components/ProgressBar/ProgressBar';

import './CourseChapterCard.scss';

export function CourseChapterCard({ course, chapter, chapterName, progress }) {
  const renderProgress = () => {
    if (!progress || progress.totalProblems === 0) {
      return null;
    }

    const { solvedProblems, totalProblems } = progress;
    return (
      <ProgressTag num={solvedProblems} denom={totalProblems}>
        {solvedProblems} / {totalProblems} solved
      </ProgressTag>
    );
  };

  const renderProgressBar = () => {
    if (!progress) {
      return null;
    }
    return <ProgressBar num={progress.solvedProblems} denom={progress.totalProblems} />;
  };

  return (
    <ContentCardLink
      to={`/courses/${course.slug}/chapters/${chapter.alias}`}
      className="course-chapter-card"
      elevation={1}
    >
      <div data-key="name">
        <h4>
          {chapter.alias}. {chapterName}
          {renderProgress()}
        </h4>
      </div>
      {renderProgressBar()}
    </ContentCardLink>
  );
}
