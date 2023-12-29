import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContestStyle } from '../../../../../../../modules/api/uriel/contest';
import { selectContest } from '../../../../modules/contestSelectors';
import ContestBundleProblemPage from './Bundle/ContestProblemPage';
import ContestProgrammingProblemPage from './Programming/ContestProblemPage';

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
