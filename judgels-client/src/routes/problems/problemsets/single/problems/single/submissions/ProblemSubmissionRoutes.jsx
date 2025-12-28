import { Route, Routes } from 'react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ProblemSubmissionsPage from './ProblemSubmissionsPage/ProblemSubmissionsPage';
import ProblemSubmissionPage from './single/ProblemSubmissionPage/ProblemSubmissionPage';

function ProblemSubmissionRoutes() {
  return (
    <div>
      <Routes>
        <Route index element={<ProblemSubmissionsPage />} />
        <Route path="mine" element={<ProblemSubmissionsPage />} />
        <Route path=":submissionId" element={<ProblemSubmissionPage />} />
      </Routes>
    </div>
  );
}

export default withBreadcrumb('Submissions')(ProblemSubmissionRoutes);
