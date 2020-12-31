import { Route, Switch } from 'react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ChapterSubmissionsPage from './ChapterSubmissionsPage/ChapterSubmissionsPage';
import ChapterSubmissionSummaryPage from './ChapterSubmissionSummaryPage/ChapterSubmissionSummaryPage';

function ChapterItemSubmissionRoutes() {
  return (
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
}

export default withBreadcrumb('Quiz results')(ChapterItemSubmissionRoutes);
