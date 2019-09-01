import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { AppState } from '../../../../../../../modules/store';
import { Contest, ContestStyle } from '../../../../../../../modules/api/uriel/contest';
import { selectContest } from '../../../../modules/contestSelectors';
import ContestProgrammingProblemPage from './Programming/ContestProblemPage';
import ContestBundleProblemPage from './Bundle/ContestProblemPage';

export interface ContestProblemPageProps extends RouteComponentProps<{ problemAlias: string }> {
  contest: Contest;
}

export class ContestProblemPage extends React.Component<ContestProblemPageProps> {
  render() {
    if (this.props.contest.style === ContestStyle.Bundle) {
      return <ContestBundleProblemPage />;
    }
    return <ContestProgrammingProblemPage />;
  }
}

export function createContestProblemPage() {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });
  return withRouter<any>(connect(mapStateToProps)(ContestProblemPage));
}

export default createContestProblemPage();
