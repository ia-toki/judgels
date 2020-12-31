import { Route, Switch } from 'react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ChapterSubmissionsPage from './ChapterSubmissionsPage/ChapterSubmissionsPage';
import ChapterSubmissionPage from './single/ChapterSubmissionPage/ChapterSubmissionPage';

function ChapterSubmissionRoutes() {
  return (
    <div>
      <Switch>
        <Route
          exact
          path="/courses/:courseSlug/chapters/:chapterAlias/submissions"
          component={ChapterSubmissionsPage}
        />
        <Route
          exact
          path="/courses/:courseSlug/chapters/:chapterAlias/submissions/mine"
          component={ChapterSubmissionsPage}
        />
        <Route
          path="/courses/:courseSlug/chapters/:chapterAlias/submissions/:submissionId"
          component={ChapterSubmissionPage}
        />
      </Switch>
    </div>
  );
}

export default withBreadcrumb('Submissions')(ChapterSubmissionRoutes);
