import { useSelector } from 'react-redux';
import { Route, Routes } from 'react-router-dom';

import { ContestRole } from '../../../../../../modules/api/uriel/contestWeb';
import { selectContestWebConfig } from '../../../modules/contestWebConfigSelectors';
import ContestSubmissionSummaryPage from './ContestSubmissionSummaryPage/ContestSubmissionSummaryPage';
import ContestSubmissionsPage from './ContestSubmissionsPage/ContestSubmissionsPage';

export default function ContestSubmissionRoutes() {
  const webConfig = useSelector(selectContestWebConfig);

  if (!webConfig) {
    return null;
  }
  if (webConfig.role === ContestRole.Contestant) {
    return <ContestSubmissionSummaryPage />;
  }
  return (
    <div>
      <Routes>
        <Route path="users/:username" element={<ContestSubmissionSummaryPage />} />
        <Route index element={<ContestSubmissionsPage />} />
      </Routes>
    </div>
  );
}
