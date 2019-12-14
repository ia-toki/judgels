import * as React from 'react';
import { Route, withRouter, Switch } from 'react-router';

import ChapterSubmissionsPage from './ChapterSubmissionsPage/ChapterSubmissionsPage';
import ChapterSubmissionPage from './single/ChapterSubmissionPage/ChapterSubmissionPage';

const ChapterSubmissionRoutes = () => (
  <div>
    <Switch>
      <Route exact path="/courses/:courseSlug/chapters/:chapterAlias/submissions" component={ChapterSubmissionsPage} />
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

export default withRouter<any, any>(ChapterSubmissionRoutes);
