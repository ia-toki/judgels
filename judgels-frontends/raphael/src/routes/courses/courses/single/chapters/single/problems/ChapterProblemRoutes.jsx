import * as React from 'react';
import { Route } from 'react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ChapterProblemsPage from './ChapterProblemsPage/ChapterProblemsPage';
import ChapterProblemPage from './single/ChapterProblemPage/ChapterProblemPage';

function ChapterProblemRoutes() {
  return (
    <div>
      <Route exact path="/courses/:courseSlug/chapters/:chapterAlias" component={ChapterProblemsPage} />
      <Route exact path="/courses/:courseSlug/chapters/:chapterAlias/problems" component={ChapterProblemsPage} />
      <Route path="/courses/:courseSlug/chapters/:chapterAlias/problems/:problemAlias" component={ChapterProblemPage} />
    </div>
  );
}

export default withBreadcrumb('Problems')(ChapterProblemRoutes);
