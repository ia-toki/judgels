import * as React from 'react';
import { withRouter, Route, Switch } from 'react-router';

import ChapterSubmissionsPage from './ChapterSubmissionsPage/ChapterSubmissionsPage';
import ChapterSubmissionSummaryPage from './ChapterSubmissionSummaryPage/ChapterSubmissionSummaryPage';

const ChapterItemSubmissionRoutes = () => (
  <div>
    <Switch>
      <Route
        path="/courses/:courseSlug/chapters/:chapterAlias/results/users/:username"
        component={ChapterSubmissionSummaryPage}
      />
      <Route path="/courses/:courseSlug/chapters/:chapterAlias/results/all" component={ChapterSubmissionsPage} />
      <Route path="/courses/:courseSlug/chapters/:chapterAlias/results" component={ChapterSubmissionSummaryPage} />
    </Switch>
  </div>
);

export default withRouter(ChapterItemSubmissionRoutes);
