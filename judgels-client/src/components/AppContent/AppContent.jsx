import classNames from 'classnames';

import './AppContent.scss';

export function AppContent({ children }) {
  return (
    <div
      className={classNames('app-content', {
        'is-course-chapter-problem': isInCourseChapterProblemPath(),
      })}
    >
      {children}
    </div>
  );
}

function isInCourseChapterProblemPath() {
  return /\/courses\/[^\/]+\/chapters\/[^\/]+\/problems\//.test(window.location.pathname);
}
