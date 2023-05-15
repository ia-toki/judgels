import { connect } from 'react-redux';
import { withRouter, Route } from 'react-router';

import ContestSubmissionsPage from './ContestSubmissionsPage/ContestSubmissionsPage';
import ContestSubmissionSummaryPage from './ContestSubmissionSummaryPage/ContestSubmissionSummaryPage';
import { selectContestWebConfig } from '../../../modules/contestWebConfigSelectors';
import { ContestRole } from '../../../../../../modules/api/uriel/contestWeb';

function ContestSubmissionRoutes({ webConfig }) {
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

const mapStateToProps = state => ({
  webConfig: selectContestWebConfig(state),
});
export default withRouter(connect(mapStateToProps)(ContestSubmissionRoutes));
