import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter, Route } from 'react-router';

import ContestSubmissionsPage from './ContestSubmissionsPage/ContestSubmissionsPage';
import ContestSubmissionSummaryPage from './ContestSubmissionSummaryPage/ContestSubmissionSummaryPage';
import { selectContestWebConfig } from '../../../modules/contestWebConfigSelectors';
import { ContestWebConfig, ContestRole } from '../../../../../../modules/api/uriel/contestWeb';

export interface ContestSubmissionRoutesProps {
  webConfig?: ContestWebConfig;
}

const ContestSubmissionRoutes: React.FunctionComponent<ContestSubmissionRoutesProps> = ({ webConfig }) => {
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
};

export const createContestSubmissionRoutes = () => {
  const mapStateToProps = state => ({
    webConfig: selectContestWebConfig(state),
  });
  return withRouter<any, any>(connect(mapStateToProps)(ContestSubmissionRoutes));
};

export default createContestSubmissionRoutes();
