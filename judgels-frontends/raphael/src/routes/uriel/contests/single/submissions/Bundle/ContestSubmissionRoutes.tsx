import * as React from 'react';
import { withRouter, Route } from 'react-router';
import ContestSubmissionsPage from './ContestSubmissionsPage/ContestSubmissionsPage';
import SubmissionSummaryPage from './SubmissionSummaryPage/SubmissionSummaryPage';
import { selectContestWebConfig } from 'routes/uriel/contests/modules/contestWebConfigSelectors';
import { connect } from 'react-redux';
import { ContestWebConfig, ContestRole } from 'modules/api/uriel/contestWeb';

export interface ContestSubmissionRoutesProps {
  webConfig?: ContestWebConfig;
}

const ContestSubmissionRoutes: React.FunctionComponent<ContestSubmissionRoutesProps> = ({ webConfig }) => {
  if (!webConfig) {
    return null;
  }
  if (webConfig.role === ContestRole.Contestant) {
    return <SubmissionSummaryPage />;
  }
  return (
    <div>
      <Route path="/contests/:contestSlug/submissions/users/:userJid" component={SubmissionSummaryPage} />
      <Route exact path="/contests/:contestSlug/submissions" component={ContestSubmissionsPage} />
    </div>
  );
};

export const createContestSubmissionRoutes = () => {
  const mapStateToProps = state => ({
    webConfig: selectContestWebConfig(state),
  });
  return withRouter(connect(mapStateToProps)(ContestSubmissionRoutes));
};

export default createContestSubmissionRoutes();
