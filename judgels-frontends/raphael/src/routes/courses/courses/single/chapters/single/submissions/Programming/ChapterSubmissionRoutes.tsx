import * as React from 'react';
import { Route, withRouter } from 'react-router';

import ChapterSubmissionsPage from './ChapterSubmissionsPage/ChapterSubmissionsPage';
import ChapterSubmissionPage from './single/ChapterSubmissionPage/ChapterSubmissionPage';

const ChapterSubmissionRoutes = () => (
  <div>
    <Route exact path="/courses/:courseSlug/chapters/:chapterAlias/submissions" component={ChapterSubmissionsPage} />
    <Route
      path="/courses/:courseSlug/chapters/:chapterAlias/submissions/:submissionId"
      component={ChapterSubmissionPage}
    />
  </div>
);

export default withRouter<any, any>(ChapterSubmissionRoutes);
