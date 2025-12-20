import { useSelector } from 'react-redux';
import { Route } from 'react-router';

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
      <Route path="/contests/:contestSlug/submissions/users/:username" component={ContestSubmissionSummaryPage} />
      <Route exact path="/contests/:contestSlug/submissions" component={ContestSubmissionsPage} />
    </div>
  );
}
