import { Route, withRouter, Switch } from 'react-router';

import ChapterProblemSubmissionsPage from './ChapterProblemSubmissionsPage/ChapterProblemSubmissionsPage';
import ChapterProblemSubmissionPage from './single/ChapterProblemSubmissionPage/ChapterProblemSubmissionPage';

function ChapterProblemSubmissionRoutes() {
  return (
    <Switch>
      <Route
        exact
        path="/courses/:courseSlug/chapters/:chapterAlias/problems/:problemAlias/submissions"
        component={ChapterProblemSubmissionsPage}
      />
      <Route
        exact
        path="/courses/:courseSlug/chapters/:chapterAlias/problems/:problemAlias/submissions/all"
        component={ChapterProblemSubmissionsPage}
      />
      <Route
        exact
        path="/courses/:courseSlug/chapters/:chapterAlias/problems/:problemAlias/submissions/:submissionId"
        component={ChapterProblemSubmissionPage}
      />
    </Switch>
  );
}

export default withRouter(ChapterProblemSubmissionRoutes);
