import * as React from 'react';
import { Route, withRouter } from 'react-router';

import ChapterSubmissionsPage from './ChapterSubmissionsPage/ChapterSubmissionsPage';

const ChapterSubmissionRoutes = () => (
  <div>
    <Route exact path="/courses/:courseSlug/chapters/:chapterAlias/submissions" component={ChapterSubmissionsPage} />
  </div>
);

export default withRouter<any, any>(ChapterSubmissionRoutes);
