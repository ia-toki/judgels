import { useSelector } from 'react-redux';
import { Outlet } from 'react-router';

import { ContestStyle } from '../../../../../modules/api/uriel/contest';
import { ContestRole } from '../../../../../modules/api/uriel/contestWeb';
import { selectContest } from '../../modules/contestSelectors';
import { selectContestWebConfig } from '../../modules/contestWebConfigSelectors';
import BundleContestSubmissionSummaryPage from './Bundle/ContestSubmissionSummaryPage/ContestSubmissionSummaryPage';
import BundleContestSubmissionsPage from './Bundle/ContestSubmissionsPage/ContestSubmissionsPage';
import ProgrammingContestSubmissionsPage from './Programming/ContestSubmissionsPage/ContestSubmissionsPage';
import ProgrammingContestSubmissionPage from './Programming/single/ContestSubmissionPage/ContestSubmissionPage';

export const contestSubmissionRoutes = [
  {
    index: true,
    element: <ContestSubmissionsPage />,
  },
  {
    path: ':submissionId',
    element: <ProgrammingContestSubmissionPage />,
  },
  {
    path: 'users/:username',
    element: <BundleContestSubmissionSummaryPage />,
  },
];

function ContestSubmissionsPage() {
  const contest = useSelector(selectContest);
  const webConfig = useSelector(selectContestWebConfig);

  if (!contest || !webConfig) {
    return null;
  }

  if (contest.style === ContestStyle.Bundle) {
    if (webConfig.role === ContestRole.Contestant) {
      return <BundleContestSubmissionSummaryPage />;
    }
    return <BundleContestSubmissionsPage />;
  }
  return <ProgrammingContestSubmissionsPage />;
}

export function ContestSubmissionLayout() {
  return (
    <div>
      <Outlet />
    </div>
  );
}
