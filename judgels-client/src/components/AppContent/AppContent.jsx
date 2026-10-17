import { Box } from '@blueprintjs/labs';
import { useLocation } from '@tanstack/react-router';
import classNames from 'classnames';

import './AppContent.scss';

export function AppContent({ children }) {
  const location = useLocation();

  return (
    <Box
      paddingX={4}
      className={classNames('app-content', {
        'is-course-chapter-problem': isInCourseChapterProblemPath(location.pathname),
      })}
    >
      {children}
    </Box>
  );
}

function isInCourseChapterProblemPath(pathname) {
  return /\/courses\/[^/]+\/chapters\/[^/]+\/problems\//.test(pathname);
}
