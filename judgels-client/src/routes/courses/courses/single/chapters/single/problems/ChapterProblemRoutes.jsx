import { Route } from 'react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ChapterProblemPage from './single/ChapterProblemPage/ChapterProblemPage';

function ChapterProblemRoutes() {
  return (
    <Route path="/courses/:courseSlug/chapters/:chapterAlias/problems/:problemAlias" component={ChapterProblemPage} />
  );
}

export default withBreadcrumb('Problems')(ChapterProblemRoutes);
