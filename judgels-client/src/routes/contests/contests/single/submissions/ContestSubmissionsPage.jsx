import { useSelector } from 'react-redux';

import { ContestStyle } from '../../../../../modules/api/uriel/contest';
import { ContestRole } from '../../../../../modules/api/uriel/contestWeb';
import { selectContest } from '../../modules/contestSelectors';
import { selectContestWebConfig } from '../../modules/contestWebConfigSelectors';
import BundleContestSubmissionSummaryPage from './Bundle/ContestSubmissionSummaryPage/ContestSubmissionSummaryPage';
import BundleContestSubmissionsPage from './Bundle/ContestSubmissionsPage/ContestSubmissionsPage';
import ProgrammingContestSubmissionsPage from './Programming/ContestSubmissionsPage/ContestSubmissionsPage';

export default function ContestSubmissionsPage() {
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
