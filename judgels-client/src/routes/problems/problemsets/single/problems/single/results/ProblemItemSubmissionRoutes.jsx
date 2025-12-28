import { Route, Routes } from 'react-router-dom';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ProblemSubmissionSummaryPage from './ProblemSubmissionSummaryPage/ProblemSubmissionSummaryPage';
import ProblemSubmissionsPage from './ProblemSubmissionsPage/ProblemSubmissionsPage';

function ProblemItemSubmissionRoutes() {
  return (
    <div>
      <Routes>
        <Route path="users/:username" element={<ProblemSubmissionSummaryPage />} />
        <Route path="all" element={<ProblemSubmissionsPage />} />
        <Route index element={<ProblemSubmissionSummaryPage />} />
      </Routes>
    </div>
  );
}

export default withBreadcrumb('Results')(ProblemItemSubmissionRoutes);
