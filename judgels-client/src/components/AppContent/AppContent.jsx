import { useLocation } from '@tanstack/react-router';
import classNames from 'classnames';

import './AppContent.scss';

export function AppContent({ children }) {
  const location = useLocation();

  return (
    <div
      className={classNames('app-content', {
        'is-course-chapter-problem': isInCourseChapterProblemPath(location.pathname),
      })}
    >
      {children}
    </div>
  );
}

function isInCourseChapterProblemPath(pathname) {
  return /\/courses\/[^/]+\/chapters\/[^/]+\/problems\//.test(pathname);
}
