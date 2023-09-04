import { Route, withRouter } from 'react-router';

import ChapterProblemSubmissionsPage from './ChapterProblemSubmissionsPage/ChapterProblemSubmissionsPage';

function ChapterProblemSubmissionRoutes() {
  return (
    <Route
      exact
      path="/courses/:courseSlug/chapters/:chapterAlias/problems/:problemAlias/submissions"
      component={ChapterProblemSubmissionsPage}
    />
  );
}

export default withRouter(ChapterProblemSubmissionRoutes);
