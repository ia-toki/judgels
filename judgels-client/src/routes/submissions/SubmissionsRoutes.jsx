import { Route, Routes } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import SubmissionsPage from './SubmissionsPage/SubmissionsPage';
import SubmissionPage from './single/SubmissionPage/SubmissionPage';

function SubmissionsRoutes() {
  return (
    <FullPageLayout>
      <Routes>
        <Route index element={<SubmissionsPage />} />
        <Route path="mine" element={<SubmissionsPage />} />
        <Route path=":submissionId" element={<SubmissionPage />} />
      </Routes>
    </FullPageLayout>
  );
}

export default withBreadcrumb('Submissions')(SubmissionsRoutes);
