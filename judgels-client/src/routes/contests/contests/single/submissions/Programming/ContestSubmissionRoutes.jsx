import { Route, Routes } from 'react-router';

import ContestSubmissionsPage from './ContestSubmissionsPage/ContestSubmissionsPage';
import ContestSubmissionPage from './single/ContestSubmissionPage/ContestSubmissionPage';

export default function ContestSubmissionRoutes() {
  return (
    <div>
      <Routes>
        <Route index element={<ContestSubmissionsPage />} />
        <Route path=":submissionId" element={<ContestSubmissionPage />} />
      </Routes>
    </div>
  );
}
