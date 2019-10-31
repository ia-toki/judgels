import * as React from 'react';
import { Route, withRouter } from 'react-router';

import ChapterProblemsPage from './ChapterProblemsPage/ChapterProblemsPage';
import ChapterProblemPage from './single/ChapterProblemPage/ChapterProblemPage';

const ChapterProblemRoutes = () => (
  <div>
    <Route exact path="/courses/:courseSlug/chapters/:chapterAlias" component={ChapterProblemsPage} />
    <Route exact path="/courses/:courseSlug/chapters/:chapterAlias/problems" component={ChapterProblemsPage} />
    <Route path="/courses/:courseSlug/chapters/:chapterAlias/problems/:problemAlias" component={ChapterProblemPage} />
  </div>
);

export default withRouter<any, any>(ChapterProblemRoutes);
