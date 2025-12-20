import { Route } from 'react-router';

import ChapterProblemSubmissionsPage from './ChapterProblemSubmissionsPage/ChapterProblemSubmissionsPage';

export default function ChapterProblemSubmissionRoutes({ worksheet, renderNavigation }) {
  return (
    <Route
      exact
      path="/courses/:courseSlug/chapters/:chapterAlias/problems/:problemAlias/submissions"
      render={props => (
        <ChapterProblemSubmissionsPage {...props} worksheet={worksheet} renderNavigation={renderNavigation} />
      )}
    />
  );
}
