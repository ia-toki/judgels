import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContestStyle } from '../../../../../../../modules/api/uriel/contest';
import { selectContest } from '../../../../modules/contestSelectors';
import ContestProgrammingProblemPage from './Programming/ContestProblemPage';
import ContestBundleProblemPage from './Bundle/ContestProblemPage';

export function ContestProblemPage({ contest }) {
  if (contest.style === ContestStyle.Bundle) {
    return <ContestBundleProblemPage />;
  }
  return <ContestProgrammingProblemPage />;
}

const mapStateToProps = state => ({
  contest: selectContest(state),
});
export default withRouter(connect(mapStateToProps)(ContestProblemPage));
