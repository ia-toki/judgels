import { connect } from 'react-redux';
import { Route, withRouter } from 'react-router';

import { ContestRole } from '../../../../../../modules/api/uriel/contestWeb';
import { selectContestWebConfig } from '../../../modules/contestWebConfigSelectors';
import ContestSubmissionSummaryPage from './ContestSubmissionSummaryPage/ContestSubmissionSummaryPage';
import ContestSubmissionsPage from './ContestSubmissionsPage/ContestSubmissionsPage';

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
