import * as React from 'react';
import { Route, withRouter } from 'react-router';

import ChapterProblemsPage from './ChapterProblemsPage/ChapterProblemsPage';

const ChapterProblemRoutes = () => (
  <div>
    <Route exact path="/courses/:courseSlug/chapters/:chapterAlias" component={ChapterProblemsPage} />
    <Route exact path="/courses/:courseSlug/chapters/:chapterAlias/problems" component={ChapterProblemsPage} />
  </div>
);

export default withRouter<any, any>(ChapterProblemRoutes);
